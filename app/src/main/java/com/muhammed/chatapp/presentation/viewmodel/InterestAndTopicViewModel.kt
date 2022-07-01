package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.data.pojo.*
import com.muhammed.chatapp.data.repository.InterestsAndTopicsRepository
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.presentation.event.InterestsAndTopicsEvent
import com.muhammed.chatapp.presentation.state.InterestsAndTopicsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestAndTopicViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val interestsAndTopicsRepository: InterestsAndTopicsRepository,


    ) : ViewModel() {
    private val selectedInterests = mutableListOf<Interest>()
    private val selectedTopics = mutableListOf<Topic>()
    private var _currentUser: User? = null
    private val _state = MutableStateFlow<InterestsAndTopicsState>(InterestsAndTopicsState.Idle)
    val state = _state.asStateFlow()

    private val viewType = MutableStateFlow<InterestAndTopic>(IdleState)

    init {
        // Deciding if we are in Interests Fragment or Topics
        tryAsync {
            viewType.collect {
                if (it is Interest) {
                    loadInterests()
                } else if (it is Topic) {
                    loadTopics()
                }
            }
        }

        // Getting current user information
        tryAsync {
            userRepository.currentUser.filterNotNull().collect { user ->
                _currentUser = user
                selectedTopics.addAll(user.topics)
                selectedInterests.addAll(user.interests)
            }
        }
    }

    fun doEvent(event: InterestsAndTopicsEvent) {
        when (event) {
            is InterestsAndTopicsEvent.InitInterestFragment -> {
                viewType.value = Interest()
            }
            is InterestsAndTopicsEvent.InitTopicFragment -> {
                viewType.value = Topic()
            }
            is InterestsAndTopicsEvent.Select -> {
                handleSelection(event.interestOrTopic)
            }
            is InterestsAndTopicsEvent.ConfirmInterestsSelection -> {
                _currentUser?.interests = selectedInterests
                updateUser()
            }
            is InterestsAndTopicsEvent.ConfirmTopicsSelection -> {
                _currentUser?.topics = selectedTopics
                updateUser()
            }

            is InterestsAndTopicsEvent.DoItLater -> {
                confirm()
            }


        }
    }

    private fun handleSelection(selection: InterestAndTopic) {
        when (selection) {
            is Interest -> {
                if (selection.isChecked) selectedInterests.add(selection)
                else selectedInterests.remove(selection)

                _state.value = if (selectedInterests.size >= 3) {
                    InterestsAndTopicsState.EnableContinueBtn
                } else {
                    InterestsAndTopicsState.DisableContinueBtn
                }
            }
            is Topic -> {
                if (selection.isChecked) selectedTopics.add(selection)
                else selectedTopics.remove(selection)

                _state.value = if (selectedTopics.size >= 3) {
                    InterestsAndTopicsState.EnableContinueBtn
                } else {
                    InterestsAndTopicsState.DisableContinueBtn
                }
            }

            else -> Unit

        }
    }


    private fun updateUser() {
        tryAsync {
            _currentUser?.let { user ->
                userRepository.updateUser(user)
                confirm()
            }
        }
    }

    private fun confirm() {
        if (viewType.value is Interest) {
            _state.value = InterestsAndTopicsState.InterestsConfirmed
        } else {
            _state.value = InterestsAndTopicsState.TopicsConfirmed
        }
    }

    private fun loadInterests() {
        tryAsync {
            interestsAndTopicsRepository.getInterests().also {
                _state.value = InterestsAndTopicsState.InterestsLoaded(it)
            }
        }
    }

    private fun loadTopics() {
        tryAsync {
            interestsAndTopicsRepository.getTopics().also {
                _state.value = InterestsAndTopicsState.TopicsLoaded(it)
            }
        }
    }

    private fun tryAsync(func: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                func()
            } catch (e: Exception) {
                _state.value = InterestsAndTopicsState.Error("Something went wrong")
            }
        }
    }

    companion object {

    }

}