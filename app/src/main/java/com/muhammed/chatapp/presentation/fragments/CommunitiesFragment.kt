package com.muhammed.chatapp.presentation.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.chip.ChipGroup
import com.muhammed.chatapp.Filter
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.databinding.FragmentCommunitiesBinding
import com.muhammed.chatapp.presentation.adapter.CommunityAdapter
import com.muhammed.chatapp.presentation.adapter.OnItemClickListener
import com.muhammed.chatapp.presentation.common.MenuOptions
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CommunitiesFragment : Fragment(), OnItemClickListener<GroupChat>,
    ChipGroup.OnCheckedStateChangeListener {
    private val binding: FragmentCommunitiesBinding by lazy {
        FragmentCommunitiesBinding.inflate(
            layoutInflater
        )
    }
    private val mForYouAdapter by lazy { CommunityAdapter().also { it.setOnItemClickListener(this) } }
    private val mAllAdapter by lazy { CommunityAdapter().also { it.setOnItemClickListener(this) } }
    private val viewModel by activityViewModels<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        viewModel.doOnEvent(ChatsEvent.EnteredCommunityFragment)

        with(binding) {
            allCommsRv.adapter = mAllAdapter
            forYouCommsRv.adapter = mForYouAdapter
            filterGroup.setOnCheckedStateChangeListener(this@CommunitiesFragment)
            filterGroup.check(viewModel.lastFilterChecked)
        }


        tryAsync {
            viewModel.userCommunities.collect {
                mForYouAdapter.submitList(it)
            }
        }

        tryAsync {
            viewModel.randomCommunitiesBasedOnInterest.collect { groups ->
                with(binding) {
                    loadingPb.visibility = View.GONE
                    allCommsRv.visibility = View.VISIBLE
                    mAllAdapter.submitList(groups)
                    root.smoothScrollBy(0, 500)
                    noCommsFound.visibility = if (groups.isEmpty()) View.VISIBLE else View.GONE
                }

            }
        }


        return binding.root
    }


    // Invoking onItemClickListener for CommunityAdapter
    override fun invoke(group: GroupChat) {
        viewModel.doOnEvent(ChatsEvent.ShowGroupDetails(group))
    }


    private fun tryAsync(function: suspend () -> Unit): Job {
        return lifecycleScope.launch {
            function()
        }
    }

    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
        with(binding) {
            var filter: Filter = Filter.Default()
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    filterAll.id -> filter = Filter.All()
                    filterArt.id -> filter = Filter.Art()
                    filterHealth.id -> filter = Filter.Health()
                    filterCrypto.id -> filter = Filter.Crypto()
                    filterFinance.id -> filter = Filter.Finance()
                    filterMovies.id -> filter = Filter.Movies()
                    filterSports.id -> filter = Filter.Sports()
                }
                viewModel.lastFilterChecked = checkedIds[0]
                changeFilterTo(filter)
            }
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
        inflater.inflate(R.menu.main_menu_community_frag, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val userIcon = menu.findItem(R.id.menu_options_comm)
        userIcon.setActionView(R.layout.user_icon)
        (userIcon.actionView as ImageView).load(viewModel.currentUser.value.profile_picture)
        userIcon.actionView.setOnClickListener {
            showOptionsMenu(it)
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