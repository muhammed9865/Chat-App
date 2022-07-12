package com.muhammed.chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.MessagingRoom
import com.muhammed.chatapp.data.pojo.chat.PrivateChat
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.databinding.ListItemChatBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.presentation.viewholder.ChatViewHolder
import dagger.assisted.AssistedInject


typealias OnItemClickListener <T> = (T) -> Unit

class ChatsAdapter @AssistedInject constructor(
    private val checkIfCurrentUserUseCase: CheckIfCurrentUserUseCase,
): ListAdapter<Chat, ChatViewHolder>(ChatDiffUtl()) {

    private var user: User? = null
    private var listener: OnItemClickListener<MessagingRoom>? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ListItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding, checkIfCurrentUserUseCase)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position) as PrivateChat, user, listener)
    }


    fun setCurrentUser(user: User) {
        this.user = user
    }

    fun registerClickListener(listener: OnItemClickListener<MessagingRoom>) {
        this.listener = listener
    }

}


class ChatDiffUtl: DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.cid == newItem.cid
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }
}