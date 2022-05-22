package com.muhammed.chatapp.presentation.fragments.auth

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentRegisterBinding
import com.muhammed.chatapp.hideKeyboard
import com.muhammed.chatapp.presentation.event.AuthenticationEvent
import com.muhammed.chatapp.presentation.state.AuthenticationState
import com.muhammed.chatapp.presentation.viewmodel.RegisterViewModel
import com.muhammed.chatapp.showError
import com.muhammed.chatapp.toggleAuthError
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
        with(binding) {
            registerToLoginBtn.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            registerBtn.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    binding.registerBtn.hideKeyboard()
                    binding.registerMotionLayout.transitionToEnd()
                    viewModel.doOnEvent(AuthenticationEvent.Submit)
                }
                false
            }

            googleSignup.setOnClickListener {
                viewModel.doOnEvent(AuthenticationEvent.StartGoogleAuthentication)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onInputFieldsTextChanged()
        onStateChanged()
    }


    private fun onStateChanged() {
        lifecycleScope.launch {
            viewModel.authStates.collect {
                when (it) {
                    is AuthenticationState.Idle -> {}

                    is AuthenticationState.AuthenticationSuccess -> {
                        findNavController().navigate(R.id.action_registerFragment_to_registerCompleteFragment)
                    }

                    is AuthenticationState.AuthenticationFailure -> {
                        binding.root.showError(it.error)
                        binding.registerMotionLayout.transitionToStart()
                    }

                    is AuthenticationState.ValidationSuccess -> {}


                    is AuthenticationState.ValidationFailure -> {
                        lifecycleScope.launch {
                            with(binding) {
                                toggleAuthError(
                                    email = registerEmail,
                                    nickname = registerNickname,
                                    password = registerPassword,
                                    repeatedPassword = registerRepeatedPassword,
                                    validationState = it.validationState
                                )
                            }
                            delay(30)
                            binding.registerMotionLayout.transitionToStart()
                        }

                    }

                    is AuthenticationState.OnGoogleAuthStart -> {
                        googleSignInActivity.launch(it.client.signInIntent)
                    }

                    is AuthenticationState.OnGoogleAuthSuccess -> {
                        it.client.signOut()
                        updateUI()
                    }

                    is AuthenticationState.OnGoogleAuthFailure -> {
                        binding.root.showError(it.error)
                    }
                }
            }
        }

    }


    private fun onInputFieldsTextChanged() = with(viewModel) {
        with(binding) {
            registerNickname.doOnTextChanged { text, _, _, _ ->
                doOnEvent(AuthenticationEvent.OnNicknameChanged(text.toString()))
            }
            registerEmail.doOnTextChanged { text, _, _, _ ->
                doOnEvent(AuthenticationEvent.OnEmailChanged(text.toString()))
                enableRegisterButton()
            }
            registerPassword.doOnTextChanged { text, _, _, _ ->
                doOnEvent(AuthenticationEvent.OnPasswordChanged(text.toString()))
                enableRegisterButton()
            }
            registerRepeatedPassword.doOnTextChanged { text, _, _, _ ->
                doOnEvent(AuthenticationEvent.OnRepeatedPassword(text.toString()))
                enableRegisterButton()
            }
        }
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

    private val googleSignInActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { data ->
                    viewModel.doOnEvent(AuthenticationEvent.OnGoogleCredentialsAvailable(data))
                }
            }
        }

    private fun updateUI() {
        with(viewModel.validation.value) {
            binding.apply {
                registerEmail.setText(email)
                registerNickname.setText(nickname)
            }
        }
    }

}