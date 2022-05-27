package com.muhammed.chatapp.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.muhammed.chatapp.databinding.ListItemMessageBinding
import com.muhammed.chatapp.pojo.Message


class MessageViewHolder(private val binding: ListItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.messageTxt.text = message.text
    }
}