package com.muhammed.chatapp.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.pojo.chat.*
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.databinding.ListItemChatBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener
import com.muhammed.chatapp.presentation.common.loadImage
import com.muhammed.chatapp.presentation.common.toDateAsString

class ChatViewHolder(
    private val binding: ListItemChatBinding,
    private val checkIfCurrentUserUseCase: CheckIfCurrentUserUseCase
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: Chat, currentUser: User?, listener: OnItemClickListener<ChatAndRoom<Chat>>?) {
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
                        val context = itemView.context
                        val membersCount = String.format(context.getString(R.string.members_count), chat.serializeMembersCount())
                        messagingRoom = messagingRoom.copy(subTitle = membersCount, isGroup = true)
                    }
                    val chatAndRoom = ChatAndRoom(chat, messagingRoom)
                    listener(chatAndRoom)
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
                    chatProfilePic.loadImage(chat.secondUser.profile_picture)
                } else {
                    chatName.text = chat.firstUser.nickname
                    chatProfilePic.loadImage(chat.firstUser.profile_picture)
                }
            }
        }
    }

    private fun setCardDetailsAsGroupChat(chat: GroupChat) {
        with(binding) {
            chatName.text = chat.title
            chatProfilePic.loadImage(chat.photo)
        }
    }


}