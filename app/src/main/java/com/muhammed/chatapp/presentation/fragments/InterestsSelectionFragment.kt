package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.pojo.Interest
import com.muhammed.chatapp.databinding.FragmentInterestsSelectionBinding
import com.muhammed.chatapp.databinding.InterestsTopicsBottomBinding
import com.muhammed.chatapp.databinding.InterestsTopicsTopbarBinding
import com.muhammed.chatapp.presentation.adapter.InterestsAndTopicsAdapter
import com.muhammed.chatapp.presentation.common.showError
import com.muhammed.chatapp.presentation.event.InterestsAndTopicsEvent
import com.muhammed.chatapp.presentation.state.InterestsAndTopicsState
import com.muhammed.chatapp.presentation.viewmodel.InterestAndTopicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterestsSelectionFragment : Fragment() {
    // Declaring Bindings to this Fragment
    private val binding by lazy { FragmentInterestsSelectionBinding.inflate(layoutInflater) }
    // TopBar (back button and logo) binding
    private val topBarBinding by lazy { InterestsTopicsTopbarBinding.bind(binding.root) }
    private val bottomBinding by lazy { InterestsTopicsBottomBinding.bind(binding.root) }
    private val viewModel by viewModels<InterestAndTopicViewModel>()
    private val mAdapter by lazy { InterestsAndTopicsAdapter(InterestsAndTopicsAdapter.INTEREST_TYPE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.doEvent(InterestsAndTopicsEvent.InitInterestFragment)

        mAdapter.setOnCheckedChangeListener {
            viewModel.doEvent(InterestsAndTopicsEvent.Select(it))
        }

        bottomBinding.apply {
            continueBtn.setOnClickListener { viewModel.doEvent(InterestsAndTopicsEvent.ConfirmInterestsSelection) }
            doItLaterBtn.setOnClickListener { viewModel.doEvent(InterestsAndTopicsEvent.DoItLater) }
        }

        binding.interestsRv.adapter = mAdapter

        onStateChanged()
        return binding.root
    }

    private fun onStateChanged() {
        tryAsync {
            viewModel.state.collect { state ->
                when (state) {
                    is InterestsAndTopicsState.InterestsLoaded -> {
                        mAdapter.submitList(state.interests)
                    }
                    is InterestsAndTopicsState.InterestsConfirmed -> {
                        findNavController().navigate(R.id.action_interestsSelectionFragment_to_topicsSelectionFragment)
                    }
                    is InterestsAndTopicsState.Error -> {
                        view?.showError(state.message)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun tryAsync(func: suspend () -> Unit) {
        lifecycleScope.launch {
            func()
        }
    }

}