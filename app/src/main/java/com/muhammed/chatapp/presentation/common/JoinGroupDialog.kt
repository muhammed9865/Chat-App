package com.muhammed.chatapp.presentation.common

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.muhammed.chatapp.databinding.DialogJoinGroupBinding

// This Dialog is shown only if it's the first time for the user in the application
class JoinGroupDialog(private val actionTakenListener: (joinPressed: Boolean) -> Unit) : BaseDialog() {
    private lateinit var binding: DialogJoinGroupBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogJoinGroupBinding.inflate(inflater, container, false).apply {
            joinCommunityBtn.setOnClickListener {
                actionTakenListener(true)
                hideDialog()
            }
        }

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        actionTakenListener(false)
    }

}