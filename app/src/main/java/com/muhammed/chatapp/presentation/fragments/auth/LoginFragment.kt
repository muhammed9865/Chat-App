package com.muhammed.chatapp.presentation.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.muhammed.chatapp.databinding.FragmentLoginBinding
import com.muhammed.chatapp.presentation.event.AuthenticationEvent
import com.muhammed.chatapp.presentation.state.AuthenticationState
import com.muhammed.chatapp.presentation.viewmodel.LoginViewModel
import com.muhammed.chatapp.showError
import com.muhammed.chatapp.toggleAuthError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInputFieldsTextChange()
        onStateChanged()
    }


    private fun onInputFieldsTextChange() {
        lifecycleScope.launch {
            with(binding) {
                loginEmail.doOnTextChanged { text, _, _, _ ->
                    viewModel.doOnEvent(AuthenticationEvent.OnEmailChanged(text.toString()))
                }
                loginPassword.doOnTextChanged { text, _, _, _ ->
                    viewModel.doOnEvent(AuthenticationEvent.OnPasswordChanged(text.toString()))
                }
            }
        }
    }

    private fun onStateChanged() {
        lifecycleScope.launch {
            viewModel.authStates.collect {
                when (it) {
                    is AuthenticationState.Idle -> {}

                    is AuthenticationState.AuthenticationSuccess -> {

                    }

                    is AuthenticationState.AuthenticationFailure -> {
                        binding.root.showError(it.error)
                        binding.loginMotionLayout.transitionToStart()
                    }

                    is AuthenticationState.ValidationSuccess -> {}


                    is AuthenticationState.ValidationFailure -> {
                        lifecycleScope.launch {
                            with(binding) {
                                toggleAuthError(
                                    email = loginEmail,
                                    password = loginPassword,
                                    validationState = it.validationState
                                )
                            }
                            delay(30)
                            binding.loginMotionLayout.transitionToStart()
                        }

                    }

                    is AuthenticationState.OnGoogleAuthStart -> {

                    }

                    is AuthenticationState.OnGoogleAuthSuccess -> {
                        it.client.signOut()

                    }

                    is AuthenticationState.OnGoogleAuthFailure -> {
                        binding.root.showError(it.error)
                    }
                }
            }
        }

    }
    }