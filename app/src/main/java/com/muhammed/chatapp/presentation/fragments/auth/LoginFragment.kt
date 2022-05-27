package com.muhammed.chatapp.presentation.fragments.auth

import android.annotation.SuppressLint
import android.app.Activity
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
import com.muhammed.chatapp.*
import com.muhammed.chatapp.databinding.FragmentLoginBinding
import com.muhammed.chatapp.presentation.common.LoadingDialog
import com.muhammed.chatapp.presentation.event.AuthenticationEvent
import com.muhammed.chatapp.presentation.state.AuthenticationState
import com.muhammed.chatapp.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog.getInstance() }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        tryLoggingInstantly()

        binding.loginBtn.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                binding.loginMotionLayout.transitionToEnd()
                viewModel.doOnEvent(AuthenticationEvent.Submit)

            }
            false
        }


        binding.googleSignin.setOnClickListener {
            viewModel.doOnEvent(AuthenticationEvent.StartGoogleAuthentication)
        }

        binding.loginToRegisterBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInputFieldsTextChange()
        onStateChanged()
    }

    private fun tryLoggingInstantly() {
        viewModel.doOnEvent(AuthenticationEvent.LoginWithSavedToken)
    }


    private fun onInputFieldsTextChange() {
        lifecycleScope.launch {
            with(binding) {
                loginEmail.doOnTextChanged { text, _, _, _ ->
                    viewModel.doOnEvent(AuthenticationEvent.OnEmailChanged(text.toString()))
                    enableLoginButton()
                }
                loginPassword.doOnTextChanged { text, _, _, _ ->
                    viewModel.doOnEvent(AuthenticationEvent.OnPasswordChanged(text.toString()))
                    enableLoginButton()
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
                        binding.loginMotionLayout.transitionToStart()

                        findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                        requireActivity().finish()
                        loadingDialog.dismiss()
                    }

                    is AuthenticationState.AuthenticationFailure -> {
                        binding.root.showError(it.error)
                        binding.loginMotionLayout.transitionToStart()
                        loadingDialog.dismissNow()
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
                        googleSignInActivity.launch(it.client.signInIntent)
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

    private fun enableLoginButton() {
        with(binding) {
            toggleButtonAvailabilityOnAuth(
                email = loginEmail,
                password = loginPassword,
                button = loginBtn
            )
        }
    }

    private val googleSignInActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { data ->
                    loadingDialog.show(requireParentFragment().parentFragmentManager, null)
                    viewModel.doOnEvent(AuthenticationEvent.OnGoogleCredentialsAvailable(data))
                }
            }
        }


}