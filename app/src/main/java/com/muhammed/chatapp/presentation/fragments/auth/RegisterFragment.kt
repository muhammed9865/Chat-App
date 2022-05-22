package com.muhammed.chatapp.presentation.fragments.auth

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentRegisterBinding
import com.muhammed.chatapp.presentation.event.RegisterEvent
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
        with(binding) {
            registerToLoginBtn.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            registerBtn.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    binding.registerBtn.hideKeyboard()
                    binding.registerMotionLayout.transitionToEnd()
                    viewModel.doOnEvent(RegisterEvent.Submit)
                }
                false
            }

            googleSignup.setOnClickListener {
                viewModel.doOnEvent(RegisterEvent.StartGoogleAuthentication)
            }
        }

        onInputFieldsTextChanged()
        onStateChanged()

        return binding.root
    }


    private fun onStateChanged() {
        lifecycleScope.launch {
            viewModel.validationStates.collect {
                when (it) {
                    is RegistrationState.Idle -> {}

                    is RegistrationState.RegistrationSuccess -> {
                        findNavController().navigate(R.id.action_registerFragment_to_registerCompleteFragment)
                    }

                    is RegistrationState.RegistrationFailure -> {
                        binding.root.showError(it.error)
                        binding.registerMotionLayout.transitionToStart()
                    }

                    is RegistrationState.ValidationSuccess -> {}


                    is RegistrationState.ValidationFailure -> {
                        lifecycleScope.launch {
                            onError(it.validationState)
                            delay(30)
                            binding.registerMotionLayout.transitionToStart()
                        }

                    }

                    is RegistrationState.OnGoogleAuthStart -> {
                        googleSignInActivity.launch(it.client.signInIntent)
                    }

                    is RegistrationState.OnGoogleAuthSuccess -> {
                        it.client.signOut()
                        updateUI()
                    }

                    is RegistrationState.OnGoogleAuthFailure -> {
                        binding.root.showError(it.error)
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
                doOnEvent(RegisterEvent.OnNicknameChanged(text.toString()))
            }
            registerEmail.doOnTextChanged { text, _, _, _ ->
                doOnEvent(RegisterEvent.OnEmailChanged(text.toString()))
                enableRegisterButton()
            }
            registerPassword.doOnTextChanged { text, _, _, _ ->
                doOnEvent(RegisterEvent.OnPasswordChanged(text.toString()))
                enableRegisterButton()
            }
            registerRepeatedPassword.doOnTextChanged { text, _, _, _ ->
                doOnEvent(RegisterEvent.OnRepeatedPassword(text.toString()))
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

    private val googleSignInActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { data ->
                    viewModel.doOnEvent(RegisterEvent.OnGoogleCredentialsAvailable(data))
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