package com.muhammed.chatapp.data.pojo

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
sealed class InterestAndTopic(
    open val title: String = "",
    @get:Exclude
    var isChecked: Boolean = false
)

class Interest(
    val imagePath: String? = null,
    title: String = ""
): InterestAndTopic(title = title) {
    // Answers the question is there an image for this interest
    fun hasImage() = !imagePath.isNullOrEmpty()
}

class Topic(
    title: String = "",
    val category: String = ""
): InterestAndTopic(title = title)

// Used in Fragments as Default State for either loading Interests or Topics
object IdleState: InterestAndTopic()