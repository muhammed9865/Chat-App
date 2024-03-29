package com.muhammed.chatapp

object Constants {
    const val CHAT = "chat"
    const val FEMALE_PATH =
        "https://firebasestorage.googleapis.com/v0/b/chat-app-6fd85.appspot.com/o/female.png?alt=media&token=654dbe26-a941-4796-a229-56aeacf0018a"
    const val MALE_PATH =
        "https://firebasestorage.googleapis.com/v0/b/chat-app-6fd85.appspot.com/o/male.png?alt=media&token=10926c90-cfb6-4a29-b2f0-1b0967c9b601"
    const val USER_TOKEN = "token"
    const val USER_CATEGORY = "user_category"
    const val USER_NAME = "user_name"
}

object Fields {
    const val LAST_MSG_DATE = "lastMessageDate"
    const val MESSAGE_DATE = "messageDate"
    const val MESSAGES = "messages"
    const val LAST_MESSAGE = "lastMessage"
}

sealed class Filter(open val title: String) {
    object Default : Filter("all")
    object All : Filter("all")
    object Movies : Filter("Movies")
    object Art : Filter("Art")
    object Sports : Filter("Sports")
    object Crypto : Filter("Crypto")
    object Finance : Filter("Finance")
    object Health : Filter(title = "Health")
    companion object {
        fun getAllCategoriesTitles(): List<String> {
            return listOf(
                Movies.title,
                Art.title,
                Sports.title,
                Crypto.title,
                Finance.title,
                Health.title
            )
        }
    }
}