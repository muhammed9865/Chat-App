package com.muhammed.chatapp.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.databinding.ListItemCommunityBinding
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener

class CommunityGroupViewHolder(private val binding: ListItemCommunityBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(group: GroupChat, onItemClickListener: OnItemClickListener<GroupChat>?) {
        with(binding) {
            if (group.hasImage()) {
                commPhoto.load(group.photo)
            }
            commCategory.text = group.category

            val res = itemView.context.resources
            val membersCount = String.format(res.getString(R.string.members_count), group.serializeMembersCount())
            commMemberCount.text = membersCount
            commTitle.text = group.title

            binding.root.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener(group)
                }
            }
        }
    }
}