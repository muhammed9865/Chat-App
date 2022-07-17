package com.muhammed.chatapp.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.muhammed.chatapp.Constants
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.ActivityMainBinding
import com.muhammed.chatapp.presentation.common.hideDialog
import com.muhammed.chatapp.presentation.common.showDialog
import com.muhammed.chatapp.presentation.common.showError
import com.muhammed.chatapp.presentation.dialogs.CreateGroupDialog
import com.muhammed.chatapp.presentation.dialogs.JoinGroupDialog
import com.muhammed.chatapp.presentation.dialogs.LoadingDialog
import com.muhammed.chatapp.presentation.dialogs.NewPrivateChat
import com.muhammed.chatapp.presentation.event.ChatsEvent
import com.muhammed.chatapp.presentation.state.ChatsState
import com.muhammed.chatapp.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    NavController.OnDestinationChangedListener {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navController: NavController by lazy {
        val host =
            supportFragmentManager.findFragmentById(R.id.fragmentsContainer) as NavHostFragment
        host.navController
    }
    private val loadingDialog by lazy { LoadingDialog.getInstance() }
    private val viewModel: MainViewModel by viewModels()
    private var signOutFromDestination: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        makeBotNavRoundedCorners()
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.title = null

        binding.botNav.setOnItemSelectedListener(this)
        navController.addOnDestinationChangedListener(this)

        binding.newChatBtn.setOnClickListener {
            createNewPrivateChat()
        }

        onStateChanged()

    }


    private fun makeBotNavRoundedCorners() {
        val radius = resources.getDimension(R.dimen.bot_nav_corners)
        val shapeDrawable: MaterialShapeDrawable =
            binding.botBar.background as MaterialShapeDrawable
        shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel.toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, radius)
            .setTopLeftCorner(CornerFamily.ROUNDED, radius)
            .build()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.chats -> navController.navigate(R.id.chatsFragment)
            R.id.community -> navController.navigate(R.id.communitiesFragment)
        }

        return true
    }

    private fun onStateChanged() {
        lifecycleScope.launch {
            viewModel.states.collect { state ->
                Log.d("Chat State", "onStateChanged from activity: ${state.javaClass}")
                when (state) {
                    is ChatsState.Idle -> {
                    }
                    is ChatsState.SignedOut -> {
                        navController.navigate(signOutFromDestination)
                        finish()
                    }
                    is ChatsState.Error -> {
                        loadingDialog.hideDialog()
                        binding.root.showError(state.error.message.toString())
                    }
                    is ChatsState.PrivateRoomCreated -> {
                        loadingDialog.hideDialog()
                    }

                    is ChatsState.EnterChat -> {
                        val intent = Intent(this@MainActivity, ChatRoomActivity::class.java)
                        intent.putExtra(Constants.CHAT, state.chatAndRoom)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                        viewModel.doOnEvent(ChatsEvent.Idle)
                    }

                    is ChatsState.FirstTime -> {
                        // Show Join Group Dialog here
                        // On any action on this dialog, send event SetFirstTimeToFalse
                        JoinGroupDialog { joinPressed ->
                            viewModel.doOnEvent(ChatsEvent.SetFirstTimeToFalse)
                            if (joinPressed) {
                                binding.botNav.selectedItemId = R.id.community
                                navController.navigate(R.id.interestsSelectionFragment)

                            }
                        }.showDialog(supportFragmentManager, null)
                    }

                    is ChatsState.Loading -> {
                        loadingDialog.showDialog(supportFragmentManager, null)
                    }

                    else -> {}
                }
            }
        }
    }


    private fun createNewPrivateChat() {
        val newChatDialog = NewPrivateChat()
        newChatDialog.setOnStartClickedListener { email ->
            viewModel.doOnEvent(ChatsEvent.CreatePrivateRoom(email))
        }
        newChatDialog.show(this.supportFragmentManager, null)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.interestsSelectionFragment, R.id.topicsSelectionFragment -> {
                toggleBotBar(show = false)
            }

            R.id.chatsFragment -> {
                signOutFromDestination = R.id.action_chatsFragment_to_authActivity
                toggleBotBar(show = true)

            }

            R.id.communitiesFragment -> {
                signOutFromDestination = R.id.action_communitiesFragment_to_authActivity
                toggleBotBar(show = true)
            }
            else -> {
                toggleBotBar(show = true)
            }
        }
    }


    private fun toggleBotBar(show: Boolean) {
        if (show) {
            with(binding) {
                botBar.visibility = View.VISIBLE
                newChatBtn.visibility = View.VISIBLE
                botNav.visibility = View.VISIBLE
                botBarDivider.setGuidelinePercent(0.92f)
            }
        } else {
            with(binding) {
                botBar.visibility = View.GONE
                botNav.visibility = View.GONE
                newChatBtn.visibility = View.GONE
                botBarDivider.setGuidelinePercent(1f)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_new_group -> {
                CreateGroupDialog().show(this.supportFragmentManager, null)
                true
            }
            R.id.menu_search_chat -> {
                true
            }
            else -> false
        }
    }
}


