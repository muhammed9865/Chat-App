package com.muhammed.chatapp

object Constants {
    const val PRIVATE_CHAT = "private_chat"
    const val FEMALE_PATH = "https://firebasestorage.googleapis.com/v0/b/chat-app-6fd85.appspot.com/o/female.png?alt=media&token=654dbe26-a941-4796-a229-56aeacf0018a"
    const val MALE_PATH = "https://firebasestorage.googleapis.com/v0/b/chat-app-6fd85.appspot.com/o/male.png?alt=media&token=10926c90-cfb6-4a29-b2f0-1b0967c9b601"
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
    data class All(override val title: String = "all") : Filter(title)
    data class Movies(override val title: String = "Movies") : Filter(title)
    data class Art(override val title: String = "Art") : Filter(title)
    data class Sports(override val title: String = "Sports") : Filter(title)
    data class Crypto(override val title: String = "Crypto") : Filter(title)
    data class Finance(override val title: String = "Finance") : Filter(title)
}