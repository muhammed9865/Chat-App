package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.ChipGroup
import com.muhammed.chatapp.Filter
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.databinding.FragmentCommunitiesBinding
import com.muhammed.chatapp.presentation.adapter.CommunityAdapter
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener
import com.muhammed.chatapp.presentation.common.MenuOptions
import com.muhammed.chatapp.presentation.dialogs.CreateGroupDialog
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CommunitiesFragment : Fragment(), OnItemClickListener<GroupChat>, ChipGroup.OnCheckedStateChangeListener {
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
            filterGroup.setOnCheckedStateChangeListener(this@CommunitiesFragment)
        }

        setHasOptionsMenu(true)

        tryAsync {
            viewModel.userCommunities.collect {
                mForYouAdapter.submitList(it)
            }
        }

        tryAsync {
            viewModel.randomCommunitiesBasedOnInterest.collect {
                with(binding) {
                    loadingPb.visibility = View.GONE
                    allCommsRv.visibility = View.VISIBLE
                }
                mAllAdapter.submitList(it)
            }
        }

        return binding.root
    }


    // Invoking onItemClickListener for CommunityAdapter
    override fun invoke(group: GroupChat) {
        viewModel.doOnEvent(ChatsEvent.ShowGroupDetails(group))
    }


    private fun tryAsync(function: suspend () -> Unit) {
        lifecycleScope.launch {
            function()
        }
    }

    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
        with(binding) {
            var filter: Filter = Filter.Default()
            when (checkedIds[0]) {
                filterAll.id -> filter = Filter.All()
                filterArt.id -> filter = Filter.Art()
                filterHealth.id -> filter = Filter.Health()
                filterCrypto.id -> filter = Filter.Crypto()
                filterFinance.id -> filter = Filter.Finance()
                filterMovies.id -> filter = Filter.Movies()
                filterSports.id -> filter = Filter.Sports()
            }
            changeFilterTo(filter)
        }
    }

    private fun changeFilterTo(filter: Filter) {
        with(binding) {
            loadingPb.visibility = View.VISIBLE
            allCommsRv.visibility = View.INVISIBLE
        }
        viewModel.doOnEvent(ChatsEvent.LoadRandomCommunitiesBasedOnFilter(filter))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu_community_frag, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_options_chat -> {
                showOptionsMenu(item.actionView.rootView)
                true
            }

            R.id.menu_new_group -> {
                CreateGroupDialog().show(requireActivity().supportFragmentManager, null)
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


}