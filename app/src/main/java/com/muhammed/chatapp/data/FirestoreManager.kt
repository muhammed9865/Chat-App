package com.muhammed.chatapp.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.chatapp.pojo.Message
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


    suspend fun createPrivateChatRoom(otherUserEmail: String): String {

        // Returned User Category as Key and the User object as Value
        val otherUser = getUser(otherUserEmail)

        val userCategory = otherUser.keys.first()
        otherUser[userCategory]?.let { user ->
            // Create Chat Document if user is not null.
            val chatDocument = mFirestore.collection(Collections.CHATS)
                .document()

            // Create Messages Document to save it's id in the PrivateChat object if user is not null
            val messagesDocument = mFirestore.collection(Collections.MESSAGES)
                .document()

            val privateChat = PrivateChat(
                cid = chatDocument.id,
                messagesId = messagesDocument.id,
                profileName = user.nickname,
                profilePicture = user.profile_picture
            )

            chatDocument.set(privateChat).await()
            messagesDocument.set(Message()).await()

            updateUserChatsList(otherUserEmail, userCategory, chatDocument.id)

            return chatDocument.id
        }

        return ""
    }

    suspend fun updateUserChatsList(userEmail: String, userCollection: String, chatId: String) {

        mFirestore.collection(userCollection)
            .document(userEmail)
            .update(UPDATES.CHATS_LIST, FieldValue.arrayUnion(chatId))
            .await()
    }


    @Throws(NullPointerException::class)
    suspend fun getUser(email: String): Map<String, User> {
        val normalUser = mFirestore.collection(Collections.USERS)
            .document(email)
            .get()
            .await().toObject(User::class.java)

        // Check if user is in Collections.USERS first
        if (normalUser != null) {
            return mapOf(Pair(Collections.USERS, normalUser))
        }

        val googleUser = getGoogleUser(email)

        // Check if user in Collections.GOOGLE_USERS if first check failed.
        if (googleUser != null) {
            return mapOf(Pair(Collections.GOOGLE_USERS, googleUser))
        }

        throw java.lang.NullPointerException("User not found")
    }

    suspend fun getUserChats(email: String, userCollection: String): List<PrivateChat> {
        val chats = mutableListOf<PrivateChat>()
        val user = mFirestore.collection(userCollection)
            .document(email)
            .get().await().toObject(User::class.java)

        user?.chats_list?.forEach {
            val room = getChat(it)
            if (room != null) {
                chats.add(room)
            }
        }

        return chats
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
