package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentInterestsSelectionBinding
import com.muhammed.chatapp.databinding.InterestsTopicsTopbarBinding

class InterestsSelectionFragment : Fragment() {
    private val binding by lazy { FragmentInterestsSelectionBinding.inflate(layoutInflater) }
    // TopBar (back button and logo) binding
    private val topBarBinding by lazy { InterestsTopicsTopbarBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

}