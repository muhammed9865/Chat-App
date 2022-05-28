package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
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
import kotlinx.coroutines.flow.filterNotNull
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
    private var profileListener: ListenerRegistration? = null


    init {
        tryAsync {
            dataStoreRepository.currentUser.filterNotNull().collect {
                _currentUser.value = it
                fireStoreRepository.setChatIds(it.chats_list)
                listenToUserChats()

            }
        }
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
        tryAsync {
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

    private  fun createPrivateChat(otherUserEmail: String) {
       tryAsync {
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
                       updateCurrentUserChatsList(room.cid)
                   }
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
        fireStoreRepository.setChatIds(usersChatList)
    }


    private fun listenToUserChats() {
        tryAsync {
            chatsListener =
                fireStoreRepository.listenToChatsChanges(_currentUser.value) { rooms ->
                    _privateChats.value = rooms
                }
        }
    }

    private fun tryAsync(function: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                function()
            }catch (e: Exception) {
                Log.d(TAG, "listenToUserProfile: ${e.message}")
                setState(ChatsState.Error("Something went wrong"))
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
        profileListener?.remove()
        super.onCleared()
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "ChatsViewModel"
    }
}