package com.muhammed.chatapp.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.muhammed.chatapp.databinding.ListItemPrivateChatBinding
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.toDateAsString
import okhttp3.internal.wait

class PrivateChatViewHolder(private val binding: ListItemPrivateChatBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: PrivateChat) {
        binding.chatLastMsg.text = chat.lastMessageText
        binding.chatLastMsgTime.text = chat.lastMessageDate.toDateAsString()
        binding.chatNewMsgsCount.text = chat.newMessagesCount.toString()
        binding.chatNewMsgsCount.visibility = if (chat.newMessagesCount == 0) View.GONE else View.VISIBLE
        binding.chatPersonName.text = chat.profileName
        binding.chatProfilePic.load(chat.profilePicture)
    }
}