package com.muhammed.chatapp.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.muhammed.chatapp.databinding.ListItemPrivateChatBinding
import com.muhammed.chatapp.domain.use_cases.ValidateCurrentUser
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User
import com.muhammed.chatapp.presentation.common.toDateAsString

class PrivateChatViewHolder(private val binding: ListItemPrivateChatBinding, private val validateCurrentUser: ValidateCurrentUser): RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: PrivateChat, currentUser: User?) {
       binding.apply {
           setCardPersonDetails(currentUser, chat)
           chatLastMsg.text = chat.lastMessageText
           chatNewMsgsCount.text = chat.newMessagesCount.toString()
           chatLastMsgTime.text = chat.lastMessageDate.toDateAsString()
           chatNewMsgsCount.visibility = if (chat.newMessagesCount > 0) View.VISIBLE else View.GONE
       }
    }


    private fun setCardPersonDetails(currentUser: User?, chat: PrivateChat) {
        // validates if chat.firstUser is the current user
        if (currentUser != null) {
            if (validateCurrentUser.execute(currentUserId = currentUser.uid, userId = chat.firstUser.uid).isSuccessful) {
                binding.chatPersonName.text = chat.secondUser.nickname
                binding.chatProfilePic.load(chat.secondUser.profile_picture)
            }else {
                binding.chatPersonName.text = chat.firstUser.nickname
                binding.chatProfilePic.load(chat.firstUser.profile_picture)
            }
        }
    }


}