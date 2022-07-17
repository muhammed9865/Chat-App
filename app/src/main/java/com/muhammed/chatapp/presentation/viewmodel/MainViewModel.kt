package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.Filter
import com.muhammed.chatapp.data.NetworkExceptions
import com.muhammed.chatapp.data.pojo.chat.*
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.data.repository.ChatsRepository
import com.muhammed.chatapp.data.repository.CommunityRepository
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.domain.use_cases.CreateRoomUseCase
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatsRepository: ChatsRepository,
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
    private val createRoomUseCase: CreateRoomUseCase,
    private val serializeEntityUseCase: SerializeEntityUseCase
) : ViewModel() {

    private val _states = MutableStateFlow<ChatsState>(ChatsState.Idle)
    val states = _states.asStateFlow()
    private var _userChats = MutableStateFlow(emptyList<Chat>())
    val userChats = _userChats.asStateFlow()

    // Used to display groups in For You Section in Community Fragment
    private val _userCommunities = MutableStateFlow(emptyList<GroupChat>())
    val userCommunities = _userCommunities.asStateFlow()

    // Used to display groups in Communities by Interest Section in Community Fragment
    private val _randomCommunitiesBasedOnInterest = Channel<List<GroupChat>>()
    val randomCommunitiesBasedOnInterest = _randomCommunitiesBasedOnInterest.receiveAsFlow()

    var lastFilterChecked = -1
    private var hasEnteredCommunityFragment = false


    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()


    private var chatsListener: ListenerRegistration? = null
    private var shouldSaveCurrChats = true
    private var currChatsList = emptyList<Chat>()


    init {
        tryAsync {
            userRepository.currentUser.filterNotNull().collect {
                _currentUser.value = it
                listenToUserChats()

                if (it.isFirstLogin) {
                    // Delaying just to make it not too quick
                    delay(2000)
                    // First time entering the app with this account
                    setState(ChatsState.FirstTime)
                }
            }
        }


    }

    fun doOnEvent(event: ChatsEvent) {
        when (event) {
            is ChatsEvent.Idle -> _states.value = ChatsState.Idle

            is ChatsEvent.SignOut -> signOut()

            is ChatsEvent.CreatePrivateRoom -> createPrivateChat(otherUserEmail = event.email)

            is ChatsEvent.JoinPrivateChat -> {
                val room = serializeEntityUseCase.toString(event.chatAndRoom)
                setState(ChatsState.EnterChat(room))
            }

            is ChatsEvent.SearchChats -> searchList(query = event.query)

            is ChatsEvent.SetFirstTimeToFalse -> {
                tryAsync {
                    _currentUser.value = _currentUser.value.copy(isFirstLogin = false)
                    userRepository.updateUser(_currentUser.value)
                }
            }

            is ChatsEvent.EnteredCommunityFragment -> {
                if (!hasEnteredCommunityFragment) {
                    loadRandomCommunitiesBasedOnFilter(Filter.All())
                    loadForUserCommunities()
                    hasEnteredCommunityFragment = true
                }
            }

            is ChatsEvent.LoadRandomCommunitiesBasedOnFilter -> loadRandomCommunitiesBasedOnFilter(
                event.filter
            )

            is ChatsEvent.ShowGroupDetails -> {
                val group = event.group
                val room = MessagingRoom(
                    chatId = group.cid,
                    messagesId = group.messagesId,
                    title = group.title,
                    subTitle = group.serializeMembersCount(),
                    isJoined = false
                )
                val chatAndRoom = ChatAndRoom<GroupChat>(group, room)
                val chatAndRoomSerialized = serializeEntityUseCase.toString(chatAndRoom)
                setState(ChatsState.EnterChat(chatAndRoomSerialized))
            }

        }
    }

    private fun createPrivateChat(otherUserEmail: String) {
        tryAsync {
            setState(ChatsState.Loading)
            // if current user is not null, start creating the chat,
            // and then update the current chat list on fireStore.
            userRepository.currentUser.filterNotNull().collect { user ->
                val userPrivateChats = _userChats.value.filterIsInstance<PrivateChat>()
                val room = createRoomUseCase.execute(otherUserEmail, user, userPrivateChats)
                room?.let {
                    userRepository.updateUserChatsList(
                        user.email,
                        user.collection,
                        room.cid
                    )
                    setState(ChatsState.PrivateRoomCreated(it))
                    updateCurrentUserChatsListState(room.cid)
                }
            }
        }
    }

    private fun searchList(query: String?) {
        tryAsync {
            // Saving the current user chats list
            if (shouldSaveCurrChats) {
                currChatsList = _userChats.value
                shouldSaveCurrChats = false
            }

            if (!query.isNullOrEmpty()) {
                val searchList = mutableListOf<Chat>()
                val currPrivateChats = _userChats.value.filterIsInstance(PrivateChat::class.java)
                val matchedPrivateChats =
                    currPrivateChats.filter {
                        it.firstUser.nickname.contains(query) || it.secondUser.nickname.contains(
                            query
                        )
                    }
                        .sortedByDescending { it.lastMessage.messageDate }

                val currGroupChats = _userChats.value.filterIsInstance(GroupChat::class.java)
                val matchedGroupChats = currGroupChats.filter { it.title.contains(query) }
                    .sortedByDescending { it.lastMessage.messageDate }

                searchList.addAll(matchedPrivateChats)
                searchList.addAll(matchedGroupChats)
                _userChats.value = searchList

            } else {
                _userChats.value = currChatsList
            }

        }
    }

    private fun updateCurrentUserChatsListState(roomId: String) {
        val usersChatList = _currentUser.value.chats_list.toMutableList()
        usersChatList.add(roomId)
        val newUser = _currentUser.value
        newUser.chats_list = usersChatList
        _currentUser.value = newUser
        tryAsync {
            userRepository.saveUserDetails(_currentUser.value)
        }
    }


    private fun listenToUserChats() {
        chatsListener =
            chatsRepository.listenToChatsChanges(_currentUser.value) { rooms ->
                _userChats.value = rooms

                // Setting to true so when user searches again, the newest list will be the saved list.
                shouldSaveCurrChats = true
            }
    }

    private fun loadRandomCommunitiesBasedOnFilter(filter: Filter) {
        tryAsync {
            communityRepository.loadCommunitiesByInterest(filter, _currentUser.value)
                .collect { groups ->
                    _randomCommunitiesBasedOnInterest.send(groups.filterNot { groupChat -> groupChat.cid in _currentUser.value.chats_list })
                }

        }
    }

    private fun loadForUserCommunities() {
        tryAsync {
            communityRepository.loadCommunitiesForUser(_currentUser.value).collect { groups ->
                _userCommunities.value =
                    groups.filterNot { groupChat -> groupChat.cid in _currentUser.value.chats_list }
            }
        }
    }

    private fun tryAsync(function: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                function()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                if (e is NetworkExceptions.NoCommunitiesFoundException) {
                    _randomCommunitiesBasedOnInterest.send(emptyList())
                }
                setState(ChatsState.Error(e))
            }
        }
    }


    private fun setState(state: ChatsState) {
        _states.value = state
    }


    private fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            setState(
                try {
                    authRepository.signOut()
                    ChatsState.SignedOut
                } catch (e: Exception) {
                    ChatsState.Error(e)
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatsListener?.remove()
        chatsListener = null
        Log.d(TAG, "onCleared: ${chatsListener == null}")
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "ChatsViewModel"
    }
}