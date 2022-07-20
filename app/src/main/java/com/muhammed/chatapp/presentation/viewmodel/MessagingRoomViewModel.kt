package com.muhammed.chatapp.presentation.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.Constants
import com.muhammed.chatapp.data.pojo.chat.*
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.data.repository.ChatsRepository
import com.muhammed.chatapp.data.repository.MessagesRepository
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import com.muhammed.chatapp.presentation.event.MessagingRoomEvents
import com.muhammed.chatapp.presentation.state.MessagingRoomActivityStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagingRoomViewModel @Inject constructor(
    private val serializeEntityUseCase: SerializeEntityUseCase,
    private val messagesRepository: MessagesRepository,
    private val userRepository: UserRepository,
    private val chatsRepository: ChatsRepository
) : ViewModel() {
    private val _room = MutableStateFlow(MessagingRoom())
    val room = _room.asStateFlow()

    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages.asStateFlow()

    private val _states =
        MutableStateFlow<MessagingRoomActivityStates>(MessagingRoomActivityStates.Idle)
    val states = _states.asStateFlow()
    var currentUser = MutableStateFlow(User())

    private var secondUser: User? = null

    private var group: GroupChat? = null
    private var private: PrivateChat? = null


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
            is MessagingRoomEvents.JoinGroup -> joinGroup(event.group)
        }
    }

    // Load the Other user details from intent
    // Intent should have extra CHAT
    // @param intent is the activity intent, passed onCreate of activity
    private fun getUserDetailsFromIntent(intent: Intent?) {
        val roomAsString = intent?.getStringExtra(Constants.CHAT)
        roomAsString?.let {

            val chatAndRoom = serializeEntityUseCase.fromString<ChatAndRoom<Chat>>(it)
            val room = chatAndRoom.room
            _room.value = room

            val isGroup = room.isGroup

            val chatAsString = serializeEntityUseCase.toString(chatAndRoom.chat)

            if (isGroup) {
                group = serializeEntityUseCase.fromString<GroupChat>(chatAsString)
            } else {
                private = serializeEntityUseCase.fromString<PrivateChat>(chatAsString)
            }


            secondUser =
                if (private?.secondUser?.email != currentUser.value.email) private?.secondUser else private?.firstUser

            if (room.isJoined) {
                listenToMessages()
            } else {
                showGroupDetails(room)
            }
        }

        // NOTE
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
            val chat = chatsRepository.getChat(room.chatId, Chat.TYPE.GROUP) as GroupChat
            setState(MessagingRoomActivityStates.ShowGroupDetails(chat))

            _room.value = room.copy(subTitle = chat.serializeMembersCount() + " Members")
            setRandomMessages(room.messagesId)
        }
    }

    private fun joinGroup(groupChat: GroupChat) {
        tryAsync {
            chatsRepository.joinGroup(groupChat, currentUser.value)
            _states.value = MessagingRoomActivityStates.JoinedGroup
            listenToMessages()
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
            Log.d(TAG, "sendMessage: sec=${secondUser?.token}")
            Log.d(TAG, "sendMessage: curr=${currentUser.value.token}")

            messagesRepository.sendMessage(
                token = secondUser?.token ?: group?.category ?: "",
                chatId = _room.value.chatId,
                messagesId = _room.value.messagesId,
                message = msg,
                group = group

            )

        }

    }

    private fun tryAsync(asyncFunction: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                asyncFunction()
            } catch (e: Exception) {
                Log.e(TAG, "tryAsync: ${e.printStackTrace()}")
                setState(MessagingRoomActivityStates.Error(e.message.toString()))
            }
        }
    }

    private fun setState(state: MessagingRoomActivityStates) {
        _states.value = state
    }

    companion object {
        private const val TAG = "MessagingViewModel"
    }

    override fun onCleared() {
        messagesRepository.unregisterMessagesListener()
        super.onCleared()
    }

}