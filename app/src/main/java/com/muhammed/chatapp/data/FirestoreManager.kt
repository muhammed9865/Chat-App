package com.muhammed.chatapp.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.muhammed.chatapp.Fields
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


    suspend fun createPrivateChatRoom(otherUserEmail: String, currentUser: User): PrivateChat? {

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

        return chatDocument.get().await().toObject(PrivateChat::class.java)
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

/*
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
*/


    fun listenToUserChats(
        user: User,
        eventListener: EventListener<DocumentSnapshot>
    ): ListenerRegistration? {
        return if (user.email.isNotEmpty())
            mFirestore.collection(user.collection)
                .document(user.email)
                .addSnapshotListener(eventListener)
        else null
    }

   suspend fun getUserChatIds(user: User): List<String>? {
        return mFirestore.collection(user.collection)
            .document(user.email)
            .get().await().toObject(User::class.java)?.chats_list
    }

    fun listenToChatsChanges(
        chat_ids: List<String>,
        listener: EventListener<QuerySnapshot>
    ): ListenerRegistration? {
        return if (chat_ids.isEmpty()) null
        else mFirestore.collection(Collections.CHATS)
            .orderBy(Fields.LAST_MSG_DATE)
            .addSnapshotListener(listener)
    }

    fun listenToChatsChangedV2(
        onChange: (room: List<DocumentChange>) -> Unit
    ) {
        mFirestore.collection(Collections.CHATS)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    value?.let {
                        if (it.documentChanges.isNotEmpty()) {
                            onChange(it.documentChanges)
                        }
                    }
                }
            }
    }

    suspend fun loadChats(chat_ids: List<String>): List<PrivateChat> {
        return if (chat_ids.isNotEmpty()) mFirestore.collection(Collections.CHATS)
            .whereIn("cid", chat_ids)
            .get()
            .await().toObjects(PrivateChat::class.java)
        else emptyList()
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
