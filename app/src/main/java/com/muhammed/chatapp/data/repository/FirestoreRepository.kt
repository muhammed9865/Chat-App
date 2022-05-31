package com.muhammed.chatapp.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.domain.use_cases.FilterUserPrivateRoom
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val fireStoreManager: FirestoreManager,
    private val filterUserPrivateRoom: FilterUserPrivateRoom,
) {


    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        fireStoreManager.createPrivateChatRoom(otherUserEmail, currentUser = currentUser) {

        }

    suspend fun updateUserChatsList(email: String, userCollection: String, chatId: String) =
        fireStoreManager.updateUserChatsList(email, userCollection, chatId)

    suspend fun getUser(userEmail: String) = fireStoreManager.getUser(userEmail)


    suspend fun getUserChatList(user: User) = fireStoreManager.getUserChatList(user)

    fun listenToChatsChanges(user: User, onChange: (rooms: List<PrivateChat>) -> Unit): ListenerRegistration {
        return fireStoreManager.listenToChatsChanges { roomsValue, roomsError ->
            if (roomsError == null) {
                roomsValue?.documents?.let { documents ->
                    val rooms = filterUserPrivateRoom.execute(documents, user)
                    onChange(rooms)
                }
            }
        }
    }


    companion object {
        private const val TAG = "FirestoreRepository"
    }

}