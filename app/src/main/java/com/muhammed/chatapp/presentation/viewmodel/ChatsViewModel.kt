package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.data.repository.DataStoreRepository
import com.muhammed.chatapp.data.repository.FirestoreRepository
import com.muhammed.chatapp.domain.use_cases.ValidateCurrentUser
import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.domain.use_cases.ValidateUserExists
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FirestoreRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val validateEmail: ValidateEmail,
    private val validateUserExists: ValidateUserExists,
    private val validateCurrentUser: ValidateCurrentUser
) : ViewModel() {
    private val _states = MutableStateFlow<ChatsState>(ChatsState.Idle)
    val states = _states.asStateFlow()
    private val _privateChats = MutableStateFlow<List<PrivateChat>>(emptyList())
    val privateChats = _privateChats.asStateFlow()
    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()


    private var chatsListener: ListenerRegistration? = null

    init {
        listenToUserChats()
    }

    fun doOnEvent(event: ChatsEvent) {
        when (event) {
            is ChatsEvent.Idle -> _states.value = ChatsState.Idle

            is ChatsEvent.SignOut -> signOut()

            is ChatsEvent.CreatePrivateRoom -> validateUserExists(email = event.email)

            else -> {}


        }
    }

    private fun validateUserExists(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.currentUser.collect { user ->
                user?.let {
                    val isCurrentUser = validateCurrentUser.execute(user.email, email)
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

    }


    /*private fun loadUserChats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setState(ChatsState.Loading)
                // Getting the current user
                val userDetails = dataStoreRepository.currentUser
                userDetails.collect { user ->
                    user?.let {
                        // loading the User chats
                        val chats = fireStoreRepository.getUserChats(user)
                        // This State will just hide the loading dialog
                        setState(ChatsState.ChatsListLoaded(currentUser = user))
                        updateUserChatsListLocally(user = it, chatsList = chats)

                    } ?: throw NullPointerException("There are no lists")
                }
            } catch (e: Exception) {
                _states.value = ChatsState.Error(e.message.toString())

            }
        }
    }*/


    /*  private suspend fun updateUserChatsListLocally(user: User, chatsList: List<String>) {
          user.chats_list = chatsList
          dataStoreRepository.saveUserDetails(user)
      }
  */
    private fun createPrivateChat(otherUserEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = validateEmail.execute(otherUserEmail)
                result.errorMessage?.let { throw NullPointerException(it) }
                setState(ChatsState.Loading)

                // if current user is not null, start creating the chat,
                // and then update the current chat list on fireStore.
                dataStoreRepository.currentUser.collect { user ->
                    user?.let {
                        val room = fireStoreRepository.createNewPrivateChat(otherUserEmail, user)
                        room?.let {
                            fireStoreRepository.updateUserChatsList(
                                user.email,
                                user.collection,
                                room.cid
                            )
                            setState(ChatsState.PrivateRoomCreated(it))
                            listenToUserChats()
                        }

                    }
                }

            } catch (e: NullPointerException) {

                setState(ChatsState.Error(e.message.toString()))
            }
        }
    }

    /* private suspend fun updateUserLocally(roomId: String) {
         val chatsIds = _currentUser.value.chats_list.toMutableList()
         chatsIds.add(roomId)
         _currentUser.value.chats_list = chatsIds
         dataStoreRepository.saveUserDetails(_currentUser.value)

     }*/

    /*private fun loadChats(chat_ids: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            setState(ChatsState.Loading)
            val chats = fireStoreRepository.loadChats(chat_ids)
            _privateChats.value = chats
            setState(ChatsState.ChatsListLoaded)
        }
    }*/

    private fun listenToUserChats() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.currentUser.collect { user ->
                user?.let {
                    _currentUser.value = it
                    fireStoreRepository.getUserChatIds(it)?.let { chatIds ->
                        // Chats Listener will be removed once a user creates or deletes a chat
                        chatsListener?.remove()
                        if (chatIds.isEmpty()) {
                            _privateChats.value = emptyList()
                        } else {
                            // Firing the Chats Listener once again with the new chats list
                            chatsListener =
                                fireStoreRepository.listenToChatsChanges(chatIds) { rooms ->
                                    _privateChats.value = rooms
                                }
                        }
                    }

                }
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
        super.onCleared()
        chatsListener?.remove()
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "ChatsViewModel"
    }
}