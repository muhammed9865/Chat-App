package com.muhammed.chatapp.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.muhammed.chatapp.databinding.ListItemPrivateChatBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.data.pojo.MessagingRoom
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener
import com.muhammed.chatapp.presentation.common.toDateAsString

class PrivateChatViewHolder(private val binding: ListItemPrivateChatBinding, private val checkIfCurrentUserUseCase: CheckIfCurrentUserUseCase): RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: PrivateChat, currentUser: User?, listener: OnItemClickListener<MessagingRoom>?) {
       binding.apply {
           setCardPersonDetails(currentUser, chat)
           chatLastMsg.text = chat.lastMessage.text
           chatNewMsgsCount.text = chat.newMessagesCount.toString()
           chatLastMsgTime.text = chat.lastMessage.messageDate.toDateAsString()
           chatNewMsgsCount.visibility = if (chat.newMessagesCount > 0) View.VISIBLE else View.GONE

           roomBackground.setOnClickListener {
               if (listener != null) {
                   val messagingRoom = MessagingRoom(
                       title = chatPersonName.text.toString(),
                       chatId = chat.cid,
                       messagesId = chat.messagesId
                   )
                   listener(messagingRoom)
               }
           }
       }
    }


    private fun setCardPersonDetails(currentUser: User?, chat: PrivateChat) {
        // validates if chat.firstUser is the current user
        if (currentUser != null) {
            if (checkIfCurrentUserUseCase.execute(currentUserEmail = currentUser.email, otherEmail = chat.firstUser.email).isSuccessful) {
                binding.chatPersonName.text = chat.secondUser.nickname
                binding.chatProfilePic.load(chat.secondUser.profile_picture)
            }else {
                binding.chatPersonName.text = chat.firstUser.nickname
                binding.chatProfilePic.load(chat.firstUser.profile_picture)
            }
        }
    }


}