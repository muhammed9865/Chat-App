package com.muhammed.chatapp.domain.use_cases

import com.google.firebase.firestore.DocumentSnapshot
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.data.pojo.chat.PrivateChat
import com.muhammed.chatapp.data.pojo.user.User
import javax.inject.Inject

class FilterUserChatRooms @Inject constructor() {
    fun execute(documents: List<DocumentSnapshot>, user: User): List<Chat> {
        val filteredDocuments =
            documents.filter { documentChange ->
               val document = documentChange.toObject(Chat::class.java)
                when (document) {
                    is PrivateChat -> document.firstUser.uid == user.uid || document.secondUser.uid == user.uid
                    is GroupChat -> document.membersIds.contains(user.email)
                    else -> false
                }
            }
                .toSet()
        return filteredDocuments.mapNotNull { dc ->
            dc.toObject(Chat::class.java)
        }

    }
}