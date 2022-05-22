package com.muhammed.chatapp.presentation.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        return binding.root
    }

}