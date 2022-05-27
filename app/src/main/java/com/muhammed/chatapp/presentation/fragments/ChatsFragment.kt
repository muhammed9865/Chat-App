package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentChatsBinding
import com.muhammed.chatapp.hideDialog
import com.muhammed.chatapp.presentation.adapter.ChatsAdapter
import com.muhammed.chatapp.presentation.common.LoadingDialog
import com.muhammed.chatapp.presentation.common.MenuOptions
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import com.muhammed.chatapp.presentation.viewmodel.ChatsViewModel
import com.muhammed.chatapp.showDialog
import com.muhammed.chatapp.showError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment() {
    private val binding: FragmentChatsBinding by lazy { FragmentChatsBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ChatsViewModel>()
    private val mAdapter: ChatsAdapter by lazy { ChatsAdapter() }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.menuBtn.setOnClickListener {
            showOptionsMenu()
        }
        binding.chatsRv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            viewModel.privateChats.collect {
                mAdapter.submitList(it)
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOnStateChanged()

    }

    private fun showOptionsMenu() {
        val menuOptions = MenuOptions(requireActivity(), binding.menuBtn, R.menu.options_menu)
        menuOptions.onSignOutSelected {
            viewModel.doOnEvent(ChatsEvent.SignOut)
        }
        menuOptions.showMenu()

    }



    private fun doOnStateChanged() {
        lifecycleScope.launch {
            viewModel.states.collect { state ->
                Log.d("Chat State", "onStateChanged from fragment: ${state.toString()}")
                when (state) {
                    is ChatsState.Idle -> {
                        loadingDialog.hideDialog()
                    }
                    is ChatsState.Loading -> {
                        loadingDialog.showDialog(parentFragmentManager, null)
                    }
                    is ChatsState.SignedOut -> {
                        requireActivity().run {
                            findNavController().navigate(R.id.action_chatsFragment_to_authActivity)
                            finish()
                        }
                    }

                    is ChatsState.PrivateRoomCreated -> {
                        viewModel.doOnEvent(ChatsEvent.LoadChats)

                    }

                    is ChatsState.ChatsListLoaded -> {
                        viewModel.doOnEvent(ChatsEvent.Idle)
                    }

                    is ChatsState.Error -> {
                        view?.showError(state.errorMessage)
                    }
                }
            }
        }
    }
}