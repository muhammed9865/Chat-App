package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentChatsBinding
import com.muhammed.chatapp.domain.use_cases.CheckIfCurrentUserUseCase
import com.muhammed.chatapp.presentation.adapter.ChatsAdapter
import com.muhammed.chatapp.presentation.common.*
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import com.muhammed.chatapp.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment() {
    private val binding: FragmentChatsBinding by lazy { FragmentChatsBinding.inflate(layoutInflater) }
    private val viewModel by activityViewModels<MainViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog.getInstance() }
    private var mAdapter: ChatsAdapter? = null

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
                viewModel.userPrivateChats.collect {
                    mAdapter?.submitList(it)
                }
            }

            launch {
                viewModel.currentUser.collect {
                    mAdapter?.setCurrentUser(it)
                }
            }
        }

        binding.chatsRv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        mAdapter?.registerClickListener {
            viewModel.doOnEvent(ChatsEvent.JoinPrivateChat(it))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //doOnStateChanged()
    }


    private fun showOptionsMenu() {
        val menuOptions = MenuOptions(requireActivity(), binding.menuBtn, R.menu.options_menu)
        menuOptions.onSignOutSelected {
            viewModel.doOnEvent(ChatsEvent.SignOut)
        }
        menuOptions.showMenu()



    }


    // Unused, States are handled in the activity MainActivity
    private fun doOnStateChanged() {

        lifecycleScope.launch {
            viewModel.states.collect { state ->
                Log.d("Chat State", "onStateChanged from fragment: $state")
                when (state) {
                    is ChatsState.SignedOut -> {
                        findNavController().navigate(R.id.action_chatsFragment_to_authActivity)
                        requireActivity().finish()
                    }

                    is ChatsState.UserExists -> {
                        Log.d(TAG, "doOnStateChanged: ${state.privateChat}")
                        loadingDialog.hideDialog()
                    }

                    is ChatsState.PrivateRoomCreated -> {
                        loadingDialog.hideDialog()
                    }

                    is ChatsState.StartListeningToRooms -> {
                        lifecycleScope.launch {
                            initializeRecyclerViewWithAdapter()
                            viewModel.currentUser.collect {
                                mAdapter?.setCurrentUser(it)
                                loadingDialog.hideDialog()
                            }
                        }
                    }
                    is ChatsState.Error -> {
                        view?.showError(state.errorMessage)
                        loadingDialog.hideDialog()
                    }

                    is ChatsState.Loading -> {
                        loadingDialog.showDialog(requireActivity().supportFragmentManager, null)
                    }
                    else -> {}
                }
            }
        }
    }


    private fun initializeRecyclerViewWithAdapter() {
        if (mAdapter == null) {
            binding.chatsRv.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    companion object {
        private const val TAG = "ChatsFragment"
    }

}