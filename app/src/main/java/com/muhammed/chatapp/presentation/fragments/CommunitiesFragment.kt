package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.databinding.FragmentCommunitiesBinding
import com.muhammed.chatapp.presentation.adapter.CommunityAdapter
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener
import com.muhammed.chatapp.presentation.common.showError
import com.muhammed.chatapp.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CommunitiesFragment : Fragment(), OnItemClickListener<GroupChat> {
    private val binding: FragmentCommunitiesBinding by lazy { FragmentCommunitiesBinding.inflate(layoutInflater) }
    private val mForYouAdapter by lazy { CommunityAdapter().also { it.setOnItemClickListener(this) } }
    private val mAllAdapter by lazy { CommunityAdapter().also { it.setOnItemClickListener(this) } }
    private val viewModel by activityViewModels<MainViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        with(binding) {
            allCommsRv.adapter = mAllAdapter
            forYouCommsRv.adapter = mForYouAdapter
        }

        tryAsync {
            viewModel.userCommunities.collect {
                mForYouAdapter.submitList(it)
            }
        }

        tryAsync {
            viewModel.randomCommunitiesBasedOnInterest.collect {
                mAllAdapter.submitList(it)
            }
        }

        return binding.root
    }

    override fun invoke(group: GroupChat) {
        binding.root.showError(group.title)
    }


    private fun tryAsync(function: suspend () -> Unit) {
        lifecycleScope.launch {
            function()
        }
    }

}