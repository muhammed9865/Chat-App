package com.muhammed.chatapp.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhammed.chatapp.databinding.DialogRestorePasswordBinding
import com.muhammed.chatapp.presentation.common.hideKeyboard
import com.muhammed.chatapp.presentation.common.showSnackBar
import com.muhammed.chatapp.presentation.state.RestorePasswordStates
import com.muhammed.chatapp.presentation.viewmodel.RestorePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class RestorePasswordDialog : Fragment() {
    private val binding by lazy { DialogRestorePasswordBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<RestorePasswordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        doOnStateChanged()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.resetEmail.doOnTextChanged { text, _, _, _ ->
            val email = text.toString()
            binding.resetBtn.isEnabled = email.isNotEmpty()
            viewModel.email = email

        }

        binding.resetBtn.setOnClickListener {
            viewModel.reset()
            it.hideKeyboard()
        }

    }

    private fun doOnStateChanged() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {
                    is RestorePasswordStates.InvalidEmail -> {
                        binding.resetEmail.error = state.error
                    }

                    is RestorePasswordStates.SendingRestoreEmail -> {
                        binding.resetEmail.error = null
                    }

                    is RestorePasswordStates.EmailSentSuccessfully -> {
                        binding.root.showSnackBar("An email was sent to ${viewModel.email}")
                        delay(2000)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}