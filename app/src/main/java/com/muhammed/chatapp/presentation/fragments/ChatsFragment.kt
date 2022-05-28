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
import com.google.firebase.firestore.Query
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.FragmentChatsBinding
import com.muhammed.chatapp.domain.use_cases.ValidateCurrentUser
import com.muhammed.chatapp.presentation.adapter.ChatsAdapter
import com.muhammed.chatapp.presentation.adapter.PrivateChatAdapter
import com.muhammed.chatapp.presentation.common.*
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import com.muhammed.chatapp.presentation.viewmodel.ChatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment() {
    private val binding: FragmentChatsBinding by lazy { FragmentChatsBinding.inflate(layoutInflater) }
    private val viewModel by activityViewModels<ChatsViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog.getInstance() }


    private var mAdapter: ChatsAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.menuBtn.setOnClickListener {
            showOptionsMenu()
        }

        mAdapter = ChatsAdapter(ValidateCurrentUser())


        lifecycleScope.launch {
            launch {
                viewModel.privateChats.collect {
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
                Log.d("Chat State", "onStateChanged from fragment: $state")
                when (state) {

                    is ChatsState.SignedOut -> {
                        findNavController().navigate(R.id.action_chatsFragment_to_authActivity)
                        requireActivity().finish()
                    }

                    is ChatsState.UserExists -> {
                        Log.d(TAG, "doOnStateChanged: ${state.privateChat}")
                    }

                    is ChatsState.PrivateRoomCreated -> {
                        loadingDialog.hideDialog()
                    }

                    is ChatsState.ChatsListLoaded -> {
                        loadingDialog.hideDialog()
                    }


                    is ChatsState.Loading -> loadingDialog.showDialog(parentFragmentManager, "loading")

                    is ChatsState.StartListeningToRooms -> {
                        lifecycleScope.launch {
                            initializeRecyclerViewWithAdapter(state.roomsQuery)
                            viewModel.currentUser.collect {
                                mAdapter?.setCurrentUser(it)
                                loadingDialog.hideDialog()
                            }
                        }
                    }
                    is ChatsState.Error -> {
                        view?.showError(state.errorMessage)
                        viewModel.doOnEvent(ChatsEvent.Idle)
                    }

                    else -> {}
                }
            }
        }
    }


    private fun initializeRecyclerViewWithAdapter(roomsQuery: Query) {
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