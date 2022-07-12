package com.muhammed.chatapp.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.data.pojo.chat.MessagingRoom
import com.muhammed.chatapp.data.pojo.chat.PrivateChat
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.databinding.ListItemChatBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener
import com.muhammed.chatapp.presentation.common.toDateAsString

class ChatViewHolder(
    private val binding: ListItemChatBinding,
    private val checkIfCurrentUserUseCase: CheckIfCurrentUserUseCase
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: Chat, currentUser: User?, listener: OnItemClickListener<MessagingRoom>?) {
        binding.apply {
            setCardDetails(currentUser, chat)
            chatSubTitle.text = chat.lastMessage.text
            chatNewMsgsCount.text = chat.newMessagesCount.toString()
            chatLastMsgTime.text = chat.lastMessage.messageDate.toDateAsString()
            chatNewMsgsCount.visibility = if (chat.newMessagesCount > 0) View.VISIBLE else View.GONE

            roomBackground.setOnClickListener {
                if (listener != null) {
                    var messagingRoom = MessagingRoom(
                        title = chatName.text.toString(),
                        chatId = chat.cid,
                        messagesId = chat.messagesId
                    )
                    if (chat is GroupChat) {
                        messagingRoom = messagingRoom.copy(subTitle = chat.serializeMembersCount())
                    }
                    listener(messagingRoom)
                }
            }
        }
    }


    private fun setCardDetails(currentUser: User?, chat: Chat) {
        when (chat) {
            is PrivateChat -> setCardDetailsAsPrivateChat(currentUser, chat)
            is GroupChat -> setCardDetailsAsGroupChat(chat)
        }
    }

    private fun setCardDetailsAsPrivateChat(currentUser: User?, chat: PrivateChat) {
        // validates if chat.firstUser is the current user
        with(binding) {
            if (currentUser != null) {
                if (checkIfCurrentUserUseCase.execute(
                        currentUserEmail = currentUser.email,
                        otherEmail = chat.firstUser.email
                    ).isSuccessful
                ) {
                    chatName.text = chat.secondUser.nickname
                    chatProfilePic.load(chat.secondUser.profile_picture)
                } else {
                    chatName.text = chat.firstUser.nickname
                    chatProfilePic.load(chat.firstUser.profile_picture)
                }
            }
        }
    }

    private fun setCardDetailsAsGroupChat(chat: GroupChat) {
        with(binding) {
            chatName.text = chat.title
        }
    }


}