package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
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

        mAdapter = ChatsAdapter(CheckIfCurrentUserUseCase())
        mAdapter.registerClickListener {
            viewModel.doOnEvent(ChatsEvent.JoinPrivateChat(it))
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu_chat_frag, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_options_chat -> {
                showOptionsMenu(item.actionView)
                true
            }


            else -> false
        }
    }

    private fun showOptionsMenu(view: View) {
        val menuOptions = MenuOptions(requireActivity(), view, R.menu.options_menu)
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