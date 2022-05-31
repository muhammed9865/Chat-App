package com.muhammed.chatapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muhammed.chatapp.data.pojo.Message
import com.muhammed.chatapp.data.pojo.User
import com.muhammed.chatapp.databinding.ListItemMessageReceivedBinding
import com.muhammed.chatapp.databinding.ListItemMessageSentBinding
import com.muhammed.chatapp.presentation.viewholder.MessageReceivedViewHolder
import com.muhammed.chatapp.presentation.viewholder.MessageSentViewHolder
import javax.inject.Inject

class MessageAdapter @Inject constructor(
    private val currentUser: User
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffUtil()) {

    override fun getItemViewType(position: Int): Int {
        val isCurrentUser = currentUser.uid == getItem(position).sender.uid
        return if (isCurrentUser) {
            SENT_VIEW_TYPE
        } else if (!isCurrentUser) {
            RECEIVED_VIEW_TYPE
        }else {
            -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("MessagesAdapter", "onCreateViewHolder: $viewType ${currentUser.email}")
        return when (viewType) {
            SENT_VIEW_TYPE -> {
                val binding =
                    ListItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MessageSentViewHolder(binding)
            }
            else -> {
                val binding =
                    ListItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MessageReceivedViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessageSentViewHolder) holder.bind(getItem(position))
        else if (holder is MessageReceivedViewHolder) holder.bind(getItem(position))
    }

    companion object {
        private const val RECEIVED_VIEW_TYPE = 1
        private const val SENT_VIEW_TYPE = 2
    }
}

class MessageDiffUtil : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.messageDate == newItem.messageDate
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
