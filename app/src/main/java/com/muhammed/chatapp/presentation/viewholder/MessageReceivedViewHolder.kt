package com.muhammed.chatapp.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.databinding.ListItemMessageReceivedBinding
import com.muhammed.chatapp.presentation.common.toDateAsString


class MessageReceivedViewHolder(private val binding: ListItemMessageReceivedBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.messageTxt.text = message.text
        binding.messageDate.text = message.messageDate.toDateAsString()
    }
}