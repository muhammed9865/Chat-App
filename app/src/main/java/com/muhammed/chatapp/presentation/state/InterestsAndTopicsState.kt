package com.muhammed.chatapp.presentation.state

import android.util.Log
import com.muhammed.chatapp.data.pojo.Interest
import com.muhammed.chatapp.data.pojo.InterestWithTopics
import com.muhammed.chatapp.data.pojo.Topic

sealed class InterestsAndTopicsState {
    object Idle: InterestsAndTopicsState()
    data class InterestsLoaded(val interests: List<Interest>) : InterestsAndTopicsState()
    data class TopicsLoaded(val topics: List<InterestWithTopics>) : InterestsAndTopicsState()
    object EnableContinueBtn : InterestsAndTopicsState()
    object DisableContinueBtn : InterestsAndTopicsState()
    object InterestsConfirmed : InterestsAndTopicsState()
    object TopicsConfirmed : InterestsAndTopicsState()
    object DoItLater : InterestsAndTopicsState()
    data class Error(val message: String) : InterestsAndTopicsState() {
            init {
                Log.e(TAG, message)
            }
            companion object {
            private const val TAG = "AuthenticationState"
        }

    }
}