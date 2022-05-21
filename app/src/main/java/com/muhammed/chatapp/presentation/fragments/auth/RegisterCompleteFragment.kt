package com.muhammed.chatapp.presentation.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentRegisterCompleteBinding


class RegisterCompleteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterCompleteBinding.inflate(layoutInflater)
        binding.goToLoginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_registerCompleteFragment_to_loginFragment)
        }
        return binding.root
    }


}