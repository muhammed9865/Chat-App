package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.coroutineScope
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

    init {
        doOnEvent(ChatsEvent.LoadChats)
    }

    fun doOnEvent(event: ChatsEvent) {
        when (event) {

            is ChatsEvent.Idle -> _states.value = ChatsState.Idle

            is ChatsEvent.SignOut -> signOut()

            is ChatsEvent.LoadChats -> {
                loadUserChats()
            }
            is ChatsEvent.CreatePrivateRoom -> validateUserExists(email = event.email)

            is ChatsEvent.JoinChat -> {}

            is ChatsEvent.SendMessage -> {}
        }
    }

    private fun validateUserExists(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.currentUser.collect { user ->
                user?.let {
                    val isCurrentUser = validateCurrentUser.execute(user.email, email)
                    if (!isCurrentUser.isSuccessful) {
                        val exists = validateUserExists.execute(email, _privateChats.value)
                        exists?.let { _states.value = ChatsState.UserExists(it) }
                            ?: createPrivateChat(email)
                    }else {
                        _states.value = ChatsState.Error("You can't add yourself")
                    }
                }
            }

        }

    }


    private fun loadUserChats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _states.value = ChatsState.Loading

                val userDetails = dataStoreRepository.currentUser
                userDetails.collect { user ->
                    user?.let {
                        val chats = fireStoreRepository.getUserChats(user)
                        _currentUser.value = user
                        _states.value = ChatsState.ChatsListLoaded(currentUser = user)
                        _privateChats.value = chats
                    } ?: throw NullPointerException("There are no lists")
                }
            } catch (e: Exception) {
                _states.value = ChatsState.Error(e.message.toString())
            }
        }
    }

    private fun createPrivateChat(otherUserEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = validateEmail.execute(otherUserEmail)
                result.errorMessage?.let { throw NullPointerException(it) }
                _states.value = ChatsState.Loading

                // if current user is not null, start creating the chat,
                // and then update the current chat list on fireStore.
                dataStoreRepository.currentUser.collect { user ->
                    user?.let {
                        val roomId = fireStoreRepository.createNewPrivateChat(otherUserEmail, user)
                        fireStoreRepository.updateUserChatsList(it.email, it.collection, roomId)

                        _states.value = ChatsState.PrivateRoomCreated
                    }
                }



            } catch (e: NullPointerException) {
                _states.value = ChatsState.Error(e.message.toString())
            }
        }
    }


    private fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            _states.value =
                try {
                    authRepository.signOut()
                    ChatsState.SignedOut
                } catch (e: Exception) {
                    ChatsState.Error(e.message!!)
                }
        }
    }
}