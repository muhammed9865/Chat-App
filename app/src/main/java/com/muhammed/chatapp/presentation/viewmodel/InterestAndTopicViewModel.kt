package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.data.pojo.*
import com.muhammed.chatapp.data.repository.InterestsAndTopicsRepository
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.presentation.event.InterestsAndTopicsEvent
import com.muhammed.chatapp.presentation.state.InterestsAndTopicsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    private val _interestsWithTopics = mutableSetOf<InterestWithTopics>()
    private val _state = MutableStateFlow<InterestsAndTopicsState>(InterestsAndTopicsState.Idle)
    val state = _state.asStateFlow()

    private val viewType = MutableStateFlow<InterestAndTopic>(IdleState)

    init {
        tryAsync {
            val userFlow = userRepository.currentUser
            val viewTypeFlow = viewType

            // ViewType depends on whether currentUser isn't null
            userFlow.combine(viewTypeFlow) { user, viewType ->
                _currentUser = user
                if (viewType is Interest) {
                    loadInterests()
                } else if (viewType is Topic) {
                    loadTopics()
                }
            }.collect()
        }


    }

    fun doEvent(event: InterestsAndTopicsEvent) {
        Log.d(TAG, event::class.java.name)
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
                setState(
                    if (selectedInterests.size >= 3) {
                        InterestsAndTopicsState.EnableContinueBtn
                    } else {
                        InterestsAndTopicsState.DisableContinueBtn
                    }
                )
            }
            is Topic -> {
                if (selection.isChecked) selectedTopics.add(selection)
                else selectedTopics.remove(selection)
                setState(
                    if (selectedTopics.size >= 4) {
                        InterestsAndTopicsState.EnableContinueBtn
                    } else {
                        InterestsAndTopicsState.DisableContinueBtn
                    }
                )
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
            setState(InterestsAndTopicsState.InterestsConfirmed)
        } else {
            setState(InterestsAndTopicsState.TopicsConfirmed)
        }
    }

    private fun loadInterests() {
        tryAsync {
            interestsAndTopicsRepository.getInterests().also {
                setState(InterestsAndTopicsState.InterestsLoaded(it))
            }
        }
    }

    private fun loadTopics() {
        tryAsync {
            _currentUser?.let { user ->
                interestsAndTopicsRepository.getUserInterestsWithTopics(user).collect {
                    it.forEach {
                        Log.d(TAG, "loadTopics: ${it.interest.title}")
                    }
                    _interestsWithTopics.addAll(it)
                    setState(InterestsAndTopicsState.TopicsLoaded(_interestsWithTopics.toList()))
                }
            } ?: Log.d(TAG, "loadTopics: null user")


        }
    }

    private fun tryAsync(func: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                func()
            } catch (e: Exception) {
                Log.d(TAG, "tryAsync: ${e.message}")
                setState(InterestsAndTopicsState.Error("Something went wrong"))
            }
        }
    }

    private fun setState(state: InterestsAndTopicsState) {
        Log.d(TAG, "setState: ${state::class.java.name}")
        _state.value = state
    }

    companion object {
        private const val TAG = "InterestAndTopicViewMod"
    }

}