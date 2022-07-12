package com.muhammed.chatapp.data.pojo.chat

import com.muhammed.chatapp.data.pojo.user.User

class GroupChat(
    cid: String = "",
    messagesId: String = "",
    val title: String = "",
    val membersCount: Int = 0,
    val description: String = "",
    val category: String = "",
    val photo: String = "",
    val membersIds: List<String> = emptyList(),
    val admins: MutableList<User> = mutableListOf(),
) : Chat(cid = cid, messagesId = messagesId) {

    fun isUserAdmin(user: User) = user in admins

    fun hasImage() = photo.isNotEmpty()
    fun serializeMembersCount(): String {
        val numAsString = membersCount.toString()
        var result = ""
        when (membersCount) {
            in 0 until 999 -> result = numAsString

            in 1000 until 10000 -> {
                result = numAsString[0] + "," + numAsString.drop(1)
            }
            in 10000 until 100000 -> {
                result = numAsString.slice(0..1) + "," + numAsString.drop(2)
            }
            in 100000 until 1000000 -> {
                result = numAsString.slice(0..2) + "," + numAsString.drop(3)
            }
        }
        return result
    }
}
