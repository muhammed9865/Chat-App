package com.muhammed.chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.databinding.ListItemCommunityBinding
import com.muhammed.chatapp.presentation.viewholder.CommunityGroupViewHolder

class CommunityAdapter: ListAdapter<GroupChat, CommunityGroupViewHolder>(CommunityDiffUtil()) {

    private var itemClickListener: OnItemClickListener<GroupChat>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityGroupViewHolder {
        val binding = ListItemCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunityGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityGroupViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<GroupChat>) {
        itemClickListener = listener
    }

    class CommunityDiffUtil : DiffUtil.ItemCallback<GroupChat>() {
        override fun areItemsTheSame(oldItem: GroupChat, newItem: GroupChat): Boolean {
            return oldItem.cid == newItem.cid
        }

        override fun areContentsTheSame(oldItem: GroupChat, newItem: GroupChat): Boolean {
            return oldItem == newItem
        }
    }
}
