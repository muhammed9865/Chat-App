package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentTopicsSelectionBinding
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
class TopicsSelectionFragment : Fragment() {
    private val binding by lazy { FragmentTopicsSelectionBinding.inflate(layoutInflater) }
    private val topBarBinding by lazy { InterestsTopicsTopbarBinding.bind(binding.root) }
    private val bottomBinding by lazy { InterestsTopicsBottomBinding.bind(binding.root) }
    private val mAdapter by lazy { InterestsAndTopicsAdapter(InterestsAndTopicsAdapter.INTEREST_WITH_TOPICS_TYPE) }
    private val viewModel by viewModels<InterestAndTopicViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.doEvent(InterestsAndTopicsEvent.InitTopicFragment)

        mAdapter.setOnCheckedChangeListener {
            viewModel.doEvent(InterestsAndTopicsEvent.Select(it))
        }
        onStateChanged()
        binding.topicsRv.adapter = mAdapter
        topBarBinding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_topicsSelectionFragment_to_interestsSelectionFragment)
        }
        bottomBinding.continueBtn.setOnClickListener { viewModel.doEvent(InterestsAndTopicsEvent.ConfirmTopicsSelection) }

        return binding.root
    }

    private fun onStateChanged() {
        tryAsync {
            viewModel.state.collect { state ->
                when (state) {
                    is InterestsAndTopicsState.TopicsLoaded -> {
                        mAdapter.submitList(state.topics)
                    }
                    is InterestsAndTopicsState.EnableContinueBtn -> {
                        bottomBinding.continueBtn.isEnabled = true
                    }
                    is InterestsAndTopicsState.DisableContinueBtn -> {
                        bottomBinding.continueBtn.isEnabled = false
                    }
                    is InterestsAndTopicsState.TopicsConfirmed, InterestsAndTopicsState.DoItLater -> {
                        findNavController().navigate(R.id.action_topicsSelectionFragment_to_communitiesFragment)
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