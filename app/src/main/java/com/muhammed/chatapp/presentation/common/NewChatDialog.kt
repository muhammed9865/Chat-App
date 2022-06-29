package com.muhammed.chatapp.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.muhammed.chatapp.databinding.DialogNewPrivateChatBinding

class NewChatDialog: DialogFragment() {
    private lateinit var binding: DialogNewPrivateChatBinding
    private var onStartClicked: ((email: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewPrivateChatBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.startBtn.setOnClickListener {
            if (onStartClicked != null) {
                onStartClicked!!(binding.emailAddress.text.toString())
                dismiss()
            }
        }
        return binding.root
    }


    fun setOnStartClickedListener(onStartClicked: (email: String) -> Unit) {
        this.onStartClicked = onStartClicked
    }
}