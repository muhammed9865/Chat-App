package com.muhammed.chatapp.presentation.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.databinding.DialogGroupDetailsBinding
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener
import com.muhammed.chatapp.presentation.common.loadImage

class GroupDetailsBottomDialog (private val chat: GroupChat) : BottomSheetDialogFragment() {
    private val binding by lazy { DialogGroupDetailsBinding.inflate(layoutInflater) }
    private var joinBtnClickListener: OnItemClickListener<GroupChat>? = null
    private var hasJoined = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind()
        return binding.root
    }

    private fun bind() {
        with(binding) {
            groupCategory.text = chat.category
            groupTitle.text = chat.title
            groupDescription.text = chat.description
            if (chat.hasImage()) {
                groupPhoto.loadImage(chat.photo)
            }
            val membersCountString = getString(R.string.members_count)
            val membersCount = String.format(membersCountString, chat.serializeMembersCount())
            groupMembersCount.text = membersCount

            joinGroupBtn.setOnClickListener {
                if (joinBtnClickListener != null) {
                    joiningPb.visibility = View.VISIBLE
                    it.isEnabled = false
                    joinBtnClickListener!!(chat)
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!hasJoined) {
            requireActivity().finish()
        }
    }

    fun onJoinedSuccessfully() {
        binding.joiningPb.visibility = View.GONE
        hasJoined = true
        dismiss()
    }

    fun setOnJoinClicked(listener: OnItemClickListener<GroupChat>) {
        this.joinBtnClickListener = listener
    }
}