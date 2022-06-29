package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentTopicsSelectionBinding

class TopicsSelectionFragment : Fragment() {
    private val binding by lazy { FragmentTopicsSelectionBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }


}