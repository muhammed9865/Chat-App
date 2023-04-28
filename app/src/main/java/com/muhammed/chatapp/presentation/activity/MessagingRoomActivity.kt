package com.muhammed.chatapp.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.muhammed.chatapp.data.pojo.chat.MessagingRoom
import com.muhammed.chatapp.databinding.ActivityChatRoomBinding
import com.muhammed.chatapp.presentation.adapter.MessageAdapter
import com.muhammed.chatapp.presentation.common.PermissionManager
import com.muhammed.chatapp.presentation.common.hideKeyboard
import com.muhammed.chatapp.presentation.common.showError
import com.muhammed.chatapp.presentation.dialogs.GroupDetailsBottomDialog
import com.muhammed.chatapp.presentation.event.MessagingRoomEvents
import com.muhammed.chatapp.presentation.state.MessagingRoomActivityStates
import com.muhammed.chatapp.presentation.viewmodel.MessagingRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessagingRoomActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChatRoomBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MessagingRoomViewModel>()
    private lateinit var mAdapter: MessageAdapter
    private var joinGroupDialog: GroupDetailsBottomDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {

            sendEvent(MessagingRoomEvents.SendUserDetails(intent))
            onStateChanged()
            toggleSendButton()
            setSupportActionBar(messagingToolbar)

            messagingToolbar.setNavigationOnClickListener { onBackPressed() }


            sendMessageBtn.setOnClickListener {
                sendEvent(
                    MessagingRoomEvents.SendMessage(
                        message = enterMsgEt.text.toString(),
                        isImage = false
                    )
                )
                enterMsgEt.text.clear()
                root.hideKeyboard()
            }

            cameraBtn.setOnClickListener {
                if (PermissionManager.hasReadStoragePermission(this@MessagingRoomActivity)) {
                    val intent = Intent().also {
                        it.type = "image/*"
                        it.action = Intent.ACTION_GET_CONTENT
                    }
                    takePhotoLauncher.launch(intent)
                }
            }

            // Collecting Messages and room details
            lifecycleScope.launch {
                launch {
                    viewModel.currentUser.collect {
                        mAdapter = MessageAdapter(it)
                        messagesRv.adapter = mAdapter
                    }
                }
                launch {
                    viewModel.room.collect {
                        updateUI(it)
                    }
                }
                launch {
                    viewModel.messages.collect {
                        mAdapter.submitList(it)
                        messagesRv.smoothScrollToPosition(mAdapter.itemCount)
                    }
                }
            }
        }
    }

    private fun toggleSendButton() {
        binding.enterMsgEt.doOnTextChanged { text, _, _, _ ->
            binding.sendMessageBtn.isEnabled = !text.isNullOrEmpty()
        }
    }


    private fun updateUI(room: MessagingRoom) {
        binding.messagingToolbar.apply {
            title = room.title
            subtitle = room.subTitle
        }
    }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                if (intent.data != null) {
                    TODO("Set the messageImageResource to the Uri then, take the image drawable and send to viewModel")
                }
            }
        }

    private fun takePhoto() {

    }

    private fun onStateChanged() {
        lifecycleScope.launch {
            viewModel.states.collect { state ->

                when (state) {
                    is MessagingRoomActivityStates.ShowGroupDetails -> {
                        joinGroupDialog = GroupDetailsBottomDialog(state.group).also {
                            it.setOnJoinClicked { chat ->
                                viewModel.doOnEvent(MessagingRoomEvents.JoinGroup(chat))
                            }
                            it.show(supportFragmentManager, null)
                        }

                    }
                    is MessagingRoomActivityStates.Error -> {
                        binding.root.showError(state.error)
                    }

                    is MessagingRoomActivityStates.JoinedGroup -> {
                        joinGroupDialog?.onJoinedSuccessfully()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun sendEvent(event: MessagingRoomEvents) {
        viewModel.doOnEvent(event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}