package com.muhammed.chatapp.presentation.fragments.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentRegisterBinding
import com.muhammed.chatapp.presentation.event.ValidationEvent
import com.muhammed.chatapp.presentation.state.RegistrationState
import com.muhammed.chatapp.presentation.state.ValidationState
import com.muhammed.chatapp.presentation.viewmodel.RegisterViewModel
import com.muhammed.hideKeyboard
import com.muhammed.showError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
    Registering Flow
    1- On Register Button clicked, a Signal to viewmodel with submission is sent.
    2- ViewModel validates the Credentials.
        -If Success, continue registering with Firebase Auth.
        -If failure, send back a failure state to Fragment.
    3- On Firebase Authentication success, a verification email will be sent to User on the registered mail.
    4- On Verification send success, User credentials will be saved in Firestore with type of User.kt

 */

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val binding: FragmentRegisterBinding by lazy {
        FragmentRegisterBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: RegisterViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.registerToLoginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerBtn.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                binding.registerBtn.hideKeyboard()
                binding.registerMotionLayout.transitionToEnd()
                viewModel.doOnEvent(ValidationEvent.Submit)
            }
            false
        }

        onInputFieldsTextChanged()
        onStateChanged()

        return binding.root
    }


    private fun onStateChanged() {
        lifecycleScope.launch {
            viewModel.registrationChannel.collect {
                when (it) {
                    is RegistrationState.RegistrationSuccess -> {
                        findNavController().navigate(R.id.action_registerFragment_to_registerCompleteFragment)
                    }

                    is RegistrationState.RegistrationFailure -> {
                        binding.root.showError(it.error)
                        // binding.registerMotionLayout.transitionToStart()
                    }

                    is RegistrationState.ValidationSuccess -> {}

                    is RegistrationState.ValidationFailure -> {
                        lifecycleScope.launch {
                            onError(it.validationState)
                            delay(50)
                            //  binding.registerMotionLayout.transitionToStart()
                        }

                    }
                }
            }
        }

    }

    private fun onError(validationState: ValidationState) {
        with(binding) {
            toggleError(registerNickname, validationState.nicknameError)
            toggleError(registerEmail, validationState.emailError)
            toggleError(registerPassword, validationState.passwordError)
            toggleError(registerRepeatedPassword, validationState.repeatedPasswordError)
        }
    }

    private fun onInputFieldsTextChanged() = with(viewModel) {
        with(binding) {
            registerNickname.doOnTextChanged { text, _, _, _ ->
                doOnEvent(ValidationEvent.OnNicknameChanged(text.toString()))
            }
            registerEmail.doOnTextChanged { text, _, _, _ ->
                doOnEvent(ValidationEvent.OnEmailChanged(text.toString()))
                enableRegisterButton()
            }
            registerPassword.doOnTextChanged { text, _, _, _ ->
                doOnEvent(ValidationEvent.OnPasswordChanged(text.toString()))
                enableRegisterButton()
            }
            registerRepeatedPassword.doOnTextChanged { text, _, _, _ ->
                doOnEvent(ValidationEvent.OnRepeatedPassword(text.toString()))
                enableRegisterButton()
            }
        }
    }

    private fun toggleError(view: EditText, mText: String?) {
        view.error = mText
    }

    private fun enableRegisterButton() {
        with(binding) {
            val isAnyEmptyField = listOf(
                registerEmail.text,
                registerPassword.text,
                registerRepeatedPassword.text
            ).any { it.isNullOrEmpty() }
            registerBtn.apply {
                isEnabled = !isAnyEmptyField
                isFocusable = !isAnyEmptyField
            }
        }
    }


}