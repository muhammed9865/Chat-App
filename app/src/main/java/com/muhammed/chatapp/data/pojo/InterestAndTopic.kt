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
) : InterestAndTopic(title = title) {
    // Answers the question is there an image for this interest
    fun hasImage() = !imagePath.isNullOrEmpty()
    override fun equals(other: Any?): Boolean {
        return title == (other as Interest).title
    }

    override fun hashCode(): Int {
        return imagePath?.hashCode() ?: 0
    }
}

class Topic(
    title: String = "",
    val category: String = ""
) : InterestAndTopic(title = title)

data class InterestWithTopics(
    val interest: Interest = Interest(),
    val topics: List<Topic> = emptyList()
) : InterestAndTopic() {
    override fun equals(other: Any?): Boolean {
        return interest.title == (other as InterestWithTopics).interest.title
    }

    override fun hashCode(): Int {
        var result = interest.hashCode()
        result = 31 * result + topics.hashCode()
        return result
    }

    companion object {
        fun create(mInterests: List<Interest>, mTopics: List<Topic>): List<InterestWithTopics> {
            val interestsWithTopics = mutableListOf<InterestWithTopics>()
            val topicsCategorizedByCategory = mTopics.groupBy { topic -> topic.category }
            mInterests.forEach { interest ->
                val topics = topicsCategorizedByCategory[interest.title]
                topics?.let {
                    val interestWithTopics = InterestWithTopics(interest = interest, topics = it)
                    interestsWithTopics.add(interestWithTopics)
                }
            }
            return interestsWithTopics
        }
    }
}

// Used in Fragments as Default State for Interests Or Topics States
object IdleState : InterestAndTopic()