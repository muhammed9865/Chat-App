package com.muhammed.chatapp.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.muhammed.chatapp.databinding.DialogSignOutBinding

class SignOutDialog: DialogFragment() {
    private lateinit var binding: DialogSignOutBinding
    private var onSignOutClicked: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSignOutBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            stayBtn.setOnClickListener {
                dismiss()
            }

            signOutBtn.setOnClickListener {
                onSignOutClicked?.let { it1 -> it1() }
            }
        }
    }

    fun setOnSignOutListener(onSignOutClicked: () -> Unit) {
       this.onSignOutClicked = onSignOutClicked
    }


}