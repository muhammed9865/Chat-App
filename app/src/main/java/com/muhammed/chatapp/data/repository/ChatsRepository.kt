package com.muhammed.chatapp.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
import com.muhammed.chatapp.domain.use_cases.FilterUserPrivateRoom
import javax.inject.Inject

class ChatsRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase,
    private val filterUserPrivateRoom: FilterUserPrivateRoom,
) {


    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        networkDatabase.createPrivateChat(otherUserEmail, currentUser = currentUser)


    fun listenToChatsChanges(user: User, onChange: (rooms: List<PrivateChat>) -> Unit): ListenerRegistration {
        return networkDatabase.listenToChatsChanges { roomsValue, roomsError ->
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