package com.muhammed.chatapp.data.repository

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.domain.use_cases.FilterUserPrivateRoom
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val fireStoreManager: FirestoreManager,
    private val filterUserPrivateRoom: FilterUserPrivateRoom,
) {


    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        fireStoreManager.createPrivateChatRoom(otherUserEmail, currentUser = currentUser) {
            chatIds.add(it)
        }

    suspend fun updateUserChatsList(email: String, userCollection: String, chatId: String) =
        fireStoreManager.updateUserChatsList(email, userCollection, chatId)

    suspend fun getUser(userEmail: String) = fireStoreManager.getUser(userEmail)

    private var chatIds = mutableListOf<String>()

    fun setChatIds(chat_ids: List<String>) {
        this.chatIds = chat_ids.toMutableList()
    }

    fun listenToChatsChanges(user: User, onChange: (rooms: List<PrivateChat>) -> Unit): ListenerRegistration {
        return fireStoreManager.listenToChatsChanges { roomsValue, roomsError ->
            if (roomsError == null) {
                Log.d(TAG, "listenToChatsChanges: $chatIds")
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