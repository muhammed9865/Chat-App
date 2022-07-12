package com.muhammed.chatapp.presentation.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.Constants
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.data.pojo.chat.MessagingRoom
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.data.repository.ChatsRepository
import com.muhammed.chatapp.data.repository.MessagesRepository
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import com.muhammed.chatapp.presentation.event.MessagingRoomEvents
import com.muhammed.chatapp.presentation.state.MessagingRoomStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsRoomViewModel @Inject constructor(
    private val serializeEntityUseCase: SerializeEntityUseCase,
    private val messagesRepository: MessagesRepository,
    private val userRepository: UserRepository,
    private val chatsRepository: ChatsRepository,
) : ViewModel() {
    private val _room = MutableStateFlow(MessagingRoom())
    val room = _room.asStateFlow()

    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages.asStateFlow()

    private val _states = MutableStateFlow<MessagingRoomStates>(MessagingRoomStates.Idle)
    val states = _states.asStateFlow()
    var currentUser = MutableStateFlow(User())
    private var messagesListener: ListenerRegistration? = null

    init {
        tryAsync {
            userRepository.currentUser.filterNotNull().collect {
                currentUser.value = it
            }
        }
    }

    fun doOnEvent(event: MessagingRoomEvents) {
        when (event) {
            is MessagingRoomEvents.SendUserDetails -> getUserDetailsFromIntent(event.intent)
            is MessagingRoomEvents.SendMessage -> sendMessage(event.message)
        }
    }

    // Load the Other user details from intent
    // Intent should have extra PRIVATE_CHAT
    // @param intent is the activity intent, passed onCreate of activity
    private fun getUserDetailsFromIntent(intent: Intent?) {
        val roomAsString = intent?.getStringExtra(Constants.PRIVATE_CHAT)
        roomAsString?.let {
            Log.d(TAG, "getUserDetailsFromIntent: $it")
            val room = serializeEntityUseCase.fromString<MessagingRoom>(it)
            _room.value = room

            if (room.isJoined) {
                listenToMessages()
            }else {

            }
        }

        // Used listenToMessages  here instead of init because I'm not sure if it will be executed first and would lead to NullPointerException
        // And since getUserDetailsFromIntent is called once entering the activity, so it's like init.

    }

    private fun listenToMessages() {
        tryAsync {
            Log.d(TAG, "listenToMessages: ${_room.value}")
            messagesRepository.listenToMessages(_room.value.messagesId) {
                _messages.value = it
                it.forEach { msg -> Log.d(TAG, "listenToMessages: ${msg.text}") }

            }
        }
    }

    // Used when user isn't part of the group but is just exploring it
    private fun showGroupDetails(room: MessagingRoom) {
        tryAsync {
            val chat =  chatsRepository.
        }
    }

    private fun setRandomMessages(messagesId: String) {
        tryAsync {
            messagesRepository.getRandomMessages(messagesId).also {
                _messages.value = it
            }
        }
    }

    private fun sendMessage(message: String) {
        tryAsync {
            val msg = Message(
                messagesId = _room.value.messagesId,
                sender = currentUser.value,
                text = message
            )
            messagesRepository.sendMessage(
                chatId = _room.value.chatId,
                messagesId = _room.value.messagesId,
                message = msg
            )
        }

    }

    private fun tryAsync(asyncFunction: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                asyncFunction()
            } catch (e: Exception) {
                Log.e(TAG, "tryAsync: ${e.message}")
                setState(MessagingRoomStates.Error(e.message.toString()))
            }
        }
    }

    private fun setState(state: MessagingRoomStates) {
        _states.value = state
    }

    companion object {
        private const val TAG = "MessagingViewModel"
    }

    override fun onCleared() {
        messagesListener?.remove()
        super.onCleared()
    }

}