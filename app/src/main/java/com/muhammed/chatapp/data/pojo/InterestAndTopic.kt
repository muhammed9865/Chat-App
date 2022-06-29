package com.muhammed.chatapp.data.pojo

import com.google.firebase.firestore.Exclude

sealed class InterestAndTopic(
    open val title: String = "",
    @Exclude
    open var isChecked: Boolean = false
)

data class Interest(
    val imagePath: String? = null,
    override val title: String
): InterestAndTopic(title = title) {
    // Answers the question is there an image for this interest
    fun doesImageExist() = !imagePath.isNullOrEmpty()
}

data class Topic(
    override val title: String,
    val category: String
): InterestAndTopic(title = title)