package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.muhammed.chatapp.databinding.FragmentCommunitiesBinding


class CommunitiesFragment : Fragment() {
    private val binding: FragmentCommunitiesBinding by lazy { FragmentCommunitiesBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

}