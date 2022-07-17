package com.muhammed.chatapp.data.pojo.user

import com.muhammed.chatapp.Constants
import com.muhammed.chatapp.data.pojo.Interest
import com.muhammed.chatapp.data.pojo.Topic

data class User(
    val uid: String = "",
    var nickname: String = "",
    val email: String = "",
    val password: String? = "",
    val token: String? = "",
    var profile_picture: String = Constants.MALE_PATH,
    val collection: String = "",
    var chats_list: List<String> = emptyList(),
    var isFirstLogin: Boolean = true,
    var interests: List<Interest> = emptyList(),
    var topics: List<Topic> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + profile_picture.hashCode()
        result = 31 * result + collection.hashCode()
        result = 31 * result + chats_list.hashCode()
        result = 31 * result + isFirstLogin.hashCode()
        result = 31 * result + interests.hashCode()
        result = 31 * result + topics.hashCode()
        return result
    }
}
