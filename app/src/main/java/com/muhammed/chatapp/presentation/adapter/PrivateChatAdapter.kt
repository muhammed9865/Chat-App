package com.muhammed.chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.muhammed.chatapp.databinding.ListItemChatBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.data.pojo.chat.PrivateChat
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.presentation.viewholder.ChatViewHolder
import javax.inject.Inject

@Deprecated("Use ChatAdapter instead")
class PrivateChatAdapter @Inject constructor(
    private val checkIfCurrentUserUseCase: CheckIfCurrentUserUseCase,
    options: Query
) : FirestoreRecyclerAdapter<PrivateChat, ChatViewHolder>(createOptions(query = options)) {
    private var user: User? = null

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
      val binding = ListItemChatBinding.inflate(LayoutInflater.from(parent.context))
      binding.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      return ChatViewHolder(binding, checkIfCurrentUserUseCase)
   }

   override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: PrivateChat) {
   }


   fun setUser(user: User) {
      this.user = user
   }

    companion object {
        private fun createOptions(query: Query) = FirestoreRecyclerOptions.Builder<PrivateChat>()
            .setQuery(query, PrivateChat::class.java)
            .build()
    }

}