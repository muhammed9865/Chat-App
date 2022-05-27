package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.data.repository.FirestoreRepository
import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.pojo.SavedUserDetails
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FirestoreRepository,
    private val validateEmail: ValidateEmail
) : ViewModel() {

    private val _states = MutableStateFlow<ChatsState>(ChatsState.Idle)
    val states = _states.asStateFlow()

    fun doOnEvent(event: ChatsEvent) {
        when (event) {
            is ChatsEvent.Idle -> _states.value = ChatsState.Idle
            is ChatsEvent.CreatePrivateRoom -> { createPrivateChat(otherUserEmail = event.email)}
            else -> {}
        }
    }

    private fun createPrivateChat(otherUserEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = validateEmail.execute(otherUserEmail)

                result.errorMessage?.let { throw NullPointerException(it) }

                _states.value = ChatsState.Loading
                val roomId = fireStoreRepository.createNewPrivateChat(otherUserEmail)
                val cUserEmail = authRepository.currentUserEmail
                val cUserCategory = authRepository.currentUserCategory

                cUserEmail.zip(cUserCategory) { uEmail, uCategory ->
                    SavedUserDetails(email = uEmail ?: "", uCategory ?: "")
                }.collect {
                    fireStoreRepository.updateUserChatsList(it.email, it.category, roomId)
                    _states.value = ChatsState.PrivateRoomCreated
                    //_states.value = ChatsState.Idle
                }

            } catch (e: NullPointerException) {
                _states.value = ChatsState.Error(e.message.toString())
            }
        }
    }
}

