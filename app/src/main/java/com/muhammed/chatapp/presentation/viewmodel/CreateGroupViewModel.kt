package com.muhammed.chatapp.presentation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.data.implementation.network.StorageNetworkImp
import com.muhammed.chatapp.data.pojo.chat.NewGroupChat
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.data.repository.ChatsRepository
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.presentation.state.CreateGroupDialogStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val chatsRepository: ChatsRepository,
    private val userRepository: UserRepository,
    private val storage: StorageNetworkImp,
): AndroidViewModel(Application()) {
    private val _states = MutableStateFlow<CreateGroupDialogStates>(CreateGroupDialogStates.Idle)

    val states = _states.asStateFlow()
    private var _currentUser: User? = null
    var title: String = ""
    var description: String = ""
    var category: String = ""
    var image: Bitmap? = null
    init {
        tryAsync {
            userRepository.currentUser.filterNotNull().collect {
                _currentUser = it
            }
        }
    }

    fun createGroup(hasInternetConnection: Boolean) {

        if (hasInternetConnection) {
            if (title.isEmpty() || description.isEmpty() || category.isEmpty() || image == null || _currentUser == null) {
                _states.value = CreateGroupDialogStates.Failed("Something went wrong")
            } else {
                tryAsync {
                    _states.value = CreateGroupDialogStates.Creating
                    val photoPath = storage.saveImage(image!!)
                    val newChat = NewGroupChat(
                        title = title,
                        description = description,
                        category = category,
                        photo = photoPath,
                        currentUser = _currentUser!!
                    )
                    chatsRepository.createGroupChat(newChat).also {
                        Log.d("CreateGroup", it.cid)
                        // Updating User chatlist with the new group id locally
                        val userChatsList = _currentUser!!.chats_list.toMutableList()
                        userChatsList.add(it.cid)
                        _currentUser = _currentUser!!.copy(chats_list = userChatsList)
                        userRepository.saveUserDetails(_currentUser!!)
                    }
                    _states.value = CreateGroupDialogStates.CreatedSuccessfully
                }
            }
        }else {
            _states.value = CreateGroupDialogStates.Failed("Check your internet connection")
        }
    }

    private fun tryAsync(function: suspend () -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            try {
                function()
            }catch (e: Exception) {
                _states.value = CreateGroupDialogStates.Failed(e.message.toString())
            }
        }
    }



}