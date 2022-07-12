package com.muhammed.chatapp.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.databinding.DialogGroupDetailsBinding
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener

class GroupDetailsBottomDialog (private val chat: GroupChat) : BottomSheetDialogFragment() {
    private val binding by lazy { DialogGroupDetailsBinding.inflate(layoutInflater) }
    private var joinBtnClickListener: OnItemClickListener<Unit>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        bind()
        return binding.root
    }

    private fun bind() {
        with(binding) {
            groupCategory.text = chat.category
            groupTitle.text = chat.title
            groupDescription.text = chat.description
            if (chat.hasImage()) {
                groupPhoto.load(chat.photo)
            }
            groupMembersCount.text = chat.serializeMembersCount()

            joinGroupBtn.setOnClickListener {
                if (joinBtnClickListener != null) {
                    joinBtnClickListener!!(Unit)
                }
            }
        }
    }

    fun setOnJoinClicked(listener: OnItemClickListener<Unit>) {
        this.joinBtnClickListener = listener
    }
}