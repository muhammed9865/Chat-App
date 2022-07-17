package com.muhammed.chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.ChatAndRoom
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
    private var listener: OnItemClickListener<ChatAndRoom<Chat>>? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ListItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding, checkIfCurrentUserUseCase)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        if (position == currentList.size - 1) {
            val lastView = holder.itemView
            val params = (lastView.layoutParams as ViewGroup.MarginLayoutParams)
            params.bottomMargin = 80
            lastView.layoutParams = params
        }
        holder.bind(getItem(position), user, listener)

    }

    override fun submitList(list: List<Chat>?) {
        super.submitList(list?.let { ArrayList(it) })
    }


    fun setCurrentUser(user: User) {
        this.user = user
    }

    fun registerClickListener(listener: OnItemClickListener<ChatAndRoom<Chat>>) {
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