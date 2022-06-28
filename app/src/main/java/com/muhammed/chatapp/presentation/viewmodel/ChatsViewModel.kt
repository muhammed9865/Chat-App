package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.data.repository.DataStoreRepository
import com.muhammed.chatapp.data.repository.NetworkRepository
import com.muhammed.chatapp.domain.use_cases.CreateRoomUseCase
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val createRoomUseCase: CreateRoomUseCase,
    private val serializeEntityUseCase: SerializeEntityUseCase
) : ViewModel() {

    private val _states = MutableStateFlow<ChatsState>(ChatsState.Idle)
    val states = _states.asStateFlow()
    private val _privateChats = MutableStateFlow<List<PrivateChat>>(emptyList())
    val privateChats = _privateChats.asStateFlow()
    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    // This State is changed whenever a private chat selected


    private var chatsListener: ListenerRegistration? = null


    init {
        tryAsync {
            dataStoreRepository.currentUser.filterNotNull().collect {
                _currentUser.value = it
                listenToUserChats()

            }
        }
    }

    fun doOnEvent(event: ChatsEvent) {
        when (event) {
            is ChatsEvent.Idle -> _states.value = ChatsState.Idle

            is ChatsEvent.SignOut -> signOut()

            is ChatsEvent.CreatePrivateRoom -> createPrivateChat(otherUserEmail = event.email)

            is ChatsEvent.JoinPrivateChat -> {
                val room = serializeEntityUseCase.toString(event.chat)
                setState(ChatsState.EnterChat(room))
            }


        }
    }

   /* private fun validateUserExists(email: String) {
        tryAsync {
            dataStoreRepository.currentUser.collect { user ->
                user?.let {
                    val isCurrentUser = checkIfCurrentUserUseCase.execute(user.email, email)
                    if (!isCurrentUser.isSuccessful) {
                        val exists = validateUserExists.execute(email, _privateChats.value)
                        exists?.let { setState(ChatsState.UserExists(it)) }
                            ?: createPrivateChat(email)
                    } else {
                        setState(ChatsState.Error("You can't add yourself"))
                    }
                }
            }
        }
    }*/

    private fun createPrivateChat(otherUserEmail: String) {
        tryAsync {
            setState(ChatsState.Loading)
            // if current user is not null, start creating the chat,
            // and then update the current chat list on fireStore.
            dataStoreRepository.currentUser.filterNotNull().collect { user ->
                val room = createRoomUseCase.execute(otherUserEmail, user, _privateChats.value)
                room?.let {
                    fireStoreRepository.updateUserChatsList(
                        user.email,
                        user.collection,
                        room.cid
                    )
                    setState(ChatsState.PrivateRoomCreated(it))
                    updateCurrentUserChatsList(room.cid)
                }
            }
        }
    }

    private fun updateCurrentUserChatsList(roomId: String) {
        val usersChatList = _currentUser.value.chats_list.toMutableList()
        usersChatList.add(roomId)
        val newUser = _currentUser.value
        newUser.chats_list = usersChatList
        _currentUser.value = newUser
    }


    private fun listenToUserChats() {
        tryAsync {
            chatsListener =
                fireStoreRepository.listenToChatsChanges(_currentUser.value) { rooms ->
                    _privateChats.value = rooms
                }
        }
    }

    private fun tryAsync (function: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                function()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                setState(ChatsState.Error(e.message.toString()))
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
                    ChatsState.Error(e.message!!)
                }
            )
        }
    }

    override fun onCleared() {
        chatsListener?.remove()
        super.onCleared()
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "ChatsViewModel"
    }
}