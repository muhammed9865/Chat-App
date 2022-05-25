package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.data.AuthRepository
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _states = MutableStateFlow<ChatsState>(ChatsState.Idle)
    val states = _states.asStateFlow()

    fun doOnEvent(event: ChatsEvent) {
        when (event) {
            is ChatsEvent.SignOut -> signOut()
        }
    }

    private fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
        _states.value =
            try {
                authRepository.signOut()
                ChatsState.SignedOut
            }catch (e: Exception) {
                ChatsState.Error(e.message!!)
            }
        }
    }
}