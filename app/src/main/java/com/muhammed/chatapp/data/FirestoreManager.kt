package com.muhammed.chatapp.data

import com.google.firebase.firestore.*
import com.muhammed.chatapp.Fields
import com.muhammed.chatapp.data.pojo.Message
import com.muhammed.chatapp.data.pojo.Messages
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
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


    suspend fun getGoogleUser(email: String): User? {
        return mFirestore.collection(Collections.GOOGLE_USERS)
            .document(email)
            .get()
            .await()
            .toObject(User::class.java)
    }



    suspend fun createPrivateChatRoom(otherUserEmail: String, currentUser: User, onRoomIdCreated: (id: String) -> Unit): PrivateChat? {
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

        onRoomIdCreated(chatDocument.id)

        updateUserChatsList(otherUserEmail, otherUser.collection, chatDocument.id)

        chatDocument.set(privateChat).await()

        messagesDocument.set(Messages()).await()

        return chatDocument.get().await().toObject(PrivateChat::class.java)
    }


    suspend fun updateUserChatsList(userEmail: String, userCollection: String, chatId: String) {
        mFirestore.collection(userCollection)
            .document(userEmail)
            .update(UPDATES.CHATS_LIST, FieldValue.arrayUnion(chatId))
            .await()
    }


    fun listenToChatsChanges(
        listener: EventListener<QuerySnapshot>
    ): ListenerRegistration {
        return mFirestore.collection(Collections.CHATS)
            .addSnapshotListener(listener)
    }

    fun listenToChatMessages(
        messagesId: String,
        listener: EventListener<DocumentSnapshot>
    ): ListenerRegistration {
        return mFirestore.collection(Collections.MESSAGES)
            .document(messagesId)
            .addSnapshotListener(listener)
    }

    suspend fun sendMessage(
        chatId: String,
        messagesId: String,
        message: Message
    ){
        mFirestore.collection(Collections.MESSAGES)
            .document(messagesId)
            .update(Fields.MESSAGES,FieldValue.arrayUnion(message))
            .await()

        mFirestore.collection(Collections.CHATS)
            .document(chatId)
            .update(Fields.LAST_MESSAGE, message)
            .await()
    }

    suspend fun getUserChatList(user: User): List<PrivateChat> {
        val chats = mutableListOf<PrivateChat>()
        val indexes = user.chats_list.chunked(10)
        indexes.forEach { listOfIds ->
            val list = mFirestore.collection(Collections.CHATS)
                .whereIn("cid", listOfIds)
                .get().await().toObjects(PrivateChat::class.java)

            list.forEach {
                chats.add(it)
            }
        }

        return chats

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
