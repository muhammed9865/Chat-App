package com.muhammed.chatapp.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.databinding.ListItemMessageSentBinding
import com.muhammed.chatapp.presentation.common.toDateAsString

class MessageSentViewHolder(private val binding: ListItemMessageSentBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.messageTxt.text = message.text
        binding.messageDate.text = message.messageDate.toDateAsString()
    }
}