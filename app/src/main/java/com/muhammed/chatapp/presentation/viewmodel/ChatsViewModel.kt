package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.data.repository.FirestoreRepository
import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.pojo.PrivateChat
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
class ChatsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FirestoreRepository,
    private val validateEmail: ValidateEmail
) : ViewModel() {
    private val _states = MutableStateFlow<ChatsState>(ChatsState.Idle)
    val states = _states.asStateFlow()

    private val _privateChats = MutableStateFlow<List<PrivateChat>>(emptyList())
    val privateChats = _privateChats.asStateFlow()

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

            is ChatsEvent.JoinChat -> {}

            is ChatsEvent.SendMessage -> {}
        }
    }


    private fun loadUserChats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _states.value = ChatsState.Loading
                val cUserEmail = authRepository.currentUserEmail
                val cUserCategory = authRepository.currentUserCategory
                // Collects both flows and convert them into SavedUserDetails object
                cUserEmail.zip(cUserCategory) { uEmail, uCategory ->
                    SavedUserDetails(email = uEmail ?: "", uCategory ?: "")
                }.collect {
                    val chats = fireStoreRepository.getUserChats(it.email, it.category)
                    _states.value = ChatsState.ChatsListLoaded(chats)
                    _privateChats.value = chats
                }
            } catch (e: Exception) {
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