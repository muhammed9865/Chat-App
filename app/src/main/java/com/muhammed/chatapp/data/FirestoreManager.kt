package com.muhammed.chatapp.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.chatapp.pojo.Messages
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreManager @Inject constructor(private val mFirestore: FirebaseFirestore) {


    suspend fun saveUser(user: User) {
        mFirestore.collection(Collections.USERS)
            .document(user.email)
            .set(user)
            .await()
    }

    suspend fun saveGoogleUser(user: User) {
        val fUser = getGoogleUser(user.uid)
        if (fUser != null) {
            throw Exception("Email address is already in use, try logging in")
        }
        mFirestore.collection(Collections.GOOGLE_USERS)
            .document(user.email)
            .set(user)
            .await()

    }


    suspend fun createPrivateChatRoom(otherUserEmail: String, currentUser: User): String {

        val otherUser = getUser(otherUserEmail)

        // Create Chat Document if user is not null.
        val chatDocument = mFirestore.collection(Collections.CHATS)
            .document()

        // Create Messages Document to save it's id in the PrivateChat object if user is not null
        val messagesDocument = mFirestore.collection(Collections.MESSAGES)
            .document()

        val privateChat = PrivateChat(
            cid = chatDocument.id,
            messagesId = messagesDocument.id,
            firstUser = currentUser,
            secondUser = otherUser
        )

        chatDocument.set(privateChat).await()

        // Setting a List to make it in array data structure on fireStore
        messagesDocument.set(Messages()).await()

        updateUserChatsList(otherUserEmail, otherUser.collection, chatDocument.id)

        return chatDocument.id
    }


    suspend fun updateUserChatsList(userEmail: String, userCollection: String, chatId: String) {
        mFirestore.collection(userCollection)
            .document(userEmail)
            .update(UPDATES.CHATS_LIST, FieldValue.arrayUnion(chatId))
            .await()
    }


    @Throws(NullPointerException::class)
    suspend fun getUser(email: String): User {
        val normalUser = mFirestore.collection(Collections.USERS)
            .document(email)
            .get()
            .await().toObject(User::class.java)

        // Check if user is in Collections.USERS first
        if (normalUser != null) {
            return normalUser
        }

        val googleUser = getGoogleUser(email)

        // Check if user in Collections.GOOGLE_USERS if first check failed.
        if (googleUser != null) {
            return googleUser
        }

        throw NullPointerException("User not found")
    }

    suspend fun getUserChats(user: User): List<PrivateChat> {
        val chats = mutableListOf<PrivateChat>()
        if (user.chats_list.isEmpty()) {
            return emptyList()
        }


        val chatsList = mFirestore.collection(user.collection)
            .document(user.email)
            .get().await().toObject(User::class.java)?.chats_list


        // Collecting User chat rooms
        chatsList?.forEach {
            val room = getChat(it)
            if (room != null) {
                chats.add(room)
            }
        }

        return chats
    }


     fun listenToChatRooms(chats_id: List<String>, onChange: (room: PrivateChat) -> Unit) {
         Log.d("ChatsViewModel", "listenToChatRooms: $chats_id")
        mFirestore.collection(Collections.CHATS)
            .whereIn("cid", chats_id)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    Log.d("ChatsViewModel", "listenToChatRooms: ${value?.documents}")
                    value?.documentChanges?.forEach {
                        onChange(it.document.toObject(PrivateChat::class.java))
                    }
                }else {
                    Log.d("ChatsViewModel", "listenToChatRooms: ${error.message}")
                }
            }
    }


    private suspend fun getChat(chatId: String): PrivateChat? {
        return mFirestore.collection(Collections.CHATS)
            .document(chatId)
            .get()
            .await()
            .toObject(PrivateChat::class.java)
    }

    suspend fun getGoogleUser(email: String): User? {
        return mFirestore.collection(Collections.GOOGLE_USERS)
            .document(email)
            .get()
            .await()
            .toObject(User::class.java)
    }

    object Collections {
        const val USERS = "users"
        const val GOOGLE_USERS = "google_users"
        const val CHATS = "chats"
        const val MESSAGES = "messages"

    }

    object UPDATES {
        const val CHATS_LIST = "chats_list"
    }
}
