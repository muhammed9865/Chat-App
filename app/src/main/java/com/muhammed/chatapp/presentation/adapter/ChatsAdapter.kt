package com.muhammed.chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.muhammed.chatapp.databinding.ListItemPrivateChatBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.data.pojo.MessagingRoom
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
import com.muhammed.chatapp.presentation.viewholder.PrivateChatViewHolder
import javax.inject.Inject


typealias OnItemClickListener <T> = (T) -> Unit

class ChatsAdapter @Inject constructor(
    private val checkIfCurrentUserUseCase: CheckIfCurrentUserUseCase,
): ListAdapter<PrivateChat, PrivateChatViewHolder>(ChatDiffUtl()) {
    private var user: User? = null
    private var listener: OnItemClickListener<MessagingRoom>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateChatViewHolder {
        val binding = ListItemPrivateChatBinding.inflate(LayoutInflater.from(parent.context))
        binding.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return PrivateChatViewHolder(binding, checkIfCurrentUserUseCase)
    }

    override fun onBindViewHolder(holder: PrivateChatViewHolder, position: Int) {
        holder.bind(getItem(position), user, listener)
    }


    fun setCurrentUser(user: User) {
        this.user = user
    }

    fun registerClickListener(listener: OnItemClickListener<MessagingRoom>) {
        this.listener = listener
    }
}

class ChatDiffUtl: DiffUtil.ItemCallback<PrivateChat>() {
    override fun areItemsTheSame(oldItem: PrivateChat, newItem: PrivateChat): Boolean {
        return oldItem.cid == newItem.cid
    }

    override fun areContentsTheSame(oldItem: PrivateChat, newItem: PrivateChat): Boolean {
        return oldItem == newItem
    }
}