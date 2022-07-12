package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentChatsBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.presentation.adapter.ChatsAdapter
import com.muhammed.chatapp.presentation.common.MenuOptions
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment() {
    private val binding: FragmentChatsBinding by lazy { FragmentChatsBinding.inflate(layoutInflater) }
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var mAdapter: ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.menuBtn.setOnClickListener {
            showOptionsMenu()
        }
        mAdapter = ChatsAdapter(CheckIfCurrentUserUseCase())

        lifecycleScope.launch {
            launch {
                viewModel.userChats.collect {
                    it.forEach { chat ->
                        Log.d("ChatsFragment", "listenToUserChats: ${chat.lastMessage.text}")
                    }
                    mAdapter.submitList(it)
                }
            }

            launch {
                viewModel.currentUser.collect {
                    mAdapter.setCurrentUser(it)
                }
            }
        }

        binding.chatsRv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        mAdapter.registerClickListener {
            viewModel.doOnEvent(ChatsEvent.JoinPrivateChat(it))
        }

        return binding.root
    }


    private fun showOptionsMenu() {
        val menuOptions = MenuOptions(requireActivity(), binding.menuBtn, R.menu.options_menu)
        menuOptions.onSignOutSelected {
            viewModel.doOnEvent(ChatsEvent.SignOut)
        }
        menuOptions.showMenu()



    }



    companion object {
        @Suppress("unused")
        private const val TAG = "ChatsFragment"
    }

}