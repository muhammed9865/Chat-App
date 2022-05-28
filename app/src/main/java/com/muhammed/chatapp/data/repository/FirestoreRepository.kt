package com.muhammed.chatapp.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val fireStoreManager: FirestoreManager
) {

    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        fireStoreManager.createPrivateChatRoom(otherUserEmail, currentUser = currentUser)

    suspend fun updateUserChatsList(email: String, userCollection: String, chatId: String) =
        fireStoreManager.updateUserChatsList(email, userCollection, chatId)

    suspend fun getUser(userEmail: String) = fireStoreManager.getUser(userEmail)

    suspend fun getUserChatIds(user: User) = fireStoreManager.getUserChatIds(user)

    fun listenToUserChats(user: User, listener: EventListener<DocumentSnapshot>) =
        fireStoreManager.listenToUserChats(user, listener)

    fun listenToChatsChanges(chat_ids: List<String>, onChange: (rooms: List<PrivateChat>) -> Unit): ListenerRegistration?{
        return fireStoreManager.listenToChatsChanges(chat_ids) { roomsValue, roomsError ->
            if (roomsError == null) {
                roomsValue?.documents?.let { documents ->
                    val filteredDocuments =
                        documents.filter { documentChange ->
                            documentChange.toObject(
                                PrivateChat::class.java
                            )?.cid in chat_ids
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


    fun listenToChatsChangesV2(
        chat_ids: List<String>,
        onChange: (rooms: List<PrivateChat>) -> Unit
    ) {
        fireStoreManager.listenToChatsChangedV2 { it ->
            val filteredDocuments =
                it.filter { documentChange -> documentChange.document.toObject(PrivateChat::class.java).cid in chat_ids }
                    .toSet()

            val rooms = filteredDocuments.map {
                it.document.toObject(PrivateChat::class.java)
            }

            onChange(rooms)

        }
    }

    suspend fun loadChats(chat_ids: List<String>) = fireStoreManager.loadChats(chat_ids)
}