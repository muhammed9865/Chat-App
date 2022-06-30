package com.muhammed.chatapp.presentation.event

import com.muhammed.chatapp.data.pojo.InterestAndTopic

sealed class InterestsAndTopicsEvent {
    object InitInterestFragment : InterestsAndTopicsEvent()
    object InitTopicFragment : InterestsAndTopicsEvent()
    object ConfirmInterestsSelection : InterestsAndTopicsEvent()
    object ConfirmTopicsSelection : InterestsAndTopicsEvent()
    object DoItLater : InterestsAndTopicsEvent()
    data class Select(val interestOrTopic: InterestAndTopic) : InterestsAndTopicsEvent()
}