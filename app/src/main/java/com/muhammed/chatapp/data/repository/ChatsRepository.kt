package com.muhammed.chatapp.data.repository

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.NewGroupChat
import com.muhammed.chatapp.data.pojo.chat.PrivateChat
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.domain.use_cases.FilterUserChatRooms
import javax.inject.Inject

class ChatsRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase,
    private val filterUserChatRooms: FilterUserChatRooms,
) {


    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        networkDatabase.createPrivateChat(otherUserEmail, currentUser = currentUser)


    fun listenToChatsChanges(user: User, onChange: (rooms: List<Chat>) -> Unit): ListenerRegistration {
        return networkDatabase.listenToChatsChanges { roomsValue, roomsError ->
            if (roomsError == null) {
                roomsValue?.documents?.let { documents ->
                    val rooms = filterUserChatRooms.execute(documents, user)
                    onChange(rooms)
                }
            }else {
                throw FirebaseFirestoreException("Something went wrong", FirebaseFirestoreException.Code.DATA_LOSS)
            }
        }
    }

    suspend fun getChat(chatId: String, chatType: Chat.TYPE) = networkDatabase.getChat(chatId, chatType)

    suspend fun createGroupChat(newGroupChat: NewGroupChat) = networkDatabase.createGroupChat(newGroupChat)

    companion object {
        private const val TAG = "FirestoreRepository"
    }

}