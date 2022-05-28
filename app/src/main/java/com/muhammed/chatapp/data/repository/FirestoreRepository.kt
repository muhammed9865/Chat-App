package com.muhammed.chatapp.data.repository

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val fireStoreManager: FirestoreManager
) {

    companion object {
        private const val TAG = "FirestoreRepository"
    }
    
    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        fireStoreManager.createPrivateChatRoom(otherUserEmail, currentUser = currentUser) {
            chatIds.add(it)
            Log.d(TAG, "createNewPrivateChat: $it")
        }

    suspend fun updateUserChatsList(email: String, userCollection: String, chatId: String) =
        fireStoreManager.updateUserChatsList(email, userCollection, chatId)

    suspend fun getUser(userEmail: String) = fireStoreManager.getUser(userEmail)



    private var chatIds = mutableListOf<String>()

    fun setChatIds(chat_ids: List<String>) {
        this.chatIds = chat_ids.toMutableList()
    }

    fun listenToChatsChanges(onChange: (rooms: List<PrivateChat>) -> Unit): ListenerRegistration{
        return fireStoreManager.listenToChatsChanges { roomsValue, roomsError ->
            if (roomsError == null) {
                Log.d(TAG, "listenToChatsChanges: $chatIds")
                roomsValue?.documents?.let { documents ->
                    val filteredDocuments =
                        documents.filter { documentChange ->
                            documentChange.toObject(
                                PrivateChat::class.java
                            )?.cid in chatIds
                        }
                            .toSet()

                    val rooms = filteredDocuments.mapNotNull { dc ->
                        dc.toObject(PrivateChat::class.java)
                    }

                   onChange(rooms)

                }
            }
        }
    }

}