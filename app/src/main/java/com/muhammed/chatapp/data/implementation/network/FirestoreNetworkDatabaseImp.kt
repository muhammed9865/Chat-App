package com.muhammed.chatapp.data.implementation.network

import android.util.Log
import com.google.firebase.firestore.*
import com.muhammed.chatapp.Fields
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.Interest
import com.muhammed.chatapp.data.pojo.InterestWithTopics
import com.muhammed.chatapp.data.pojo.Topic
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.data.pojo.chat.NewGroupChat
import com.muhammed.chatapp.data.pojo.chat.PrivateChat
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.data.pojo.message.Messages
import com.muhammed.chatapp.data.pojo.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreNetworkDatabaseImp @Inject constructor(private val mFirestore: FirebaseFirestore) :
    NetworkDatabase {

    private var lastVisibleCommunityDocument: DocumentSnapshot? = null

    override suspend fun saveUser(user: User) {
        mFirestore.collection(Collections.USERS)
            .document(user.email)
            .set(user)
            .await()
    }

    override suspend fun saveGoogleUser(user: User) {
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
    override suspend fun getUser(email: String): User {
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


    override suspend fun getGoogleUser(email: String): User? {
        return mFirestore.collection(Collections.GOOGLE_USERS)
            .document(email)
            .get()
            .await()
            .toObject(User::class.java)
    }

    override suspend fun getChat(chatId: String, chatType: Chat.TYPE): Chat {
        val chat = mFirestore.collection(Collections.CHATS).document(chatId).get().await()

        if (chat.data == null) {
            throw NullPointerException("Chat is not found")
        }
        return when (chatType) {
            Chat.TYPE.PRIVATE -> chat.toObject(PrivateChat::class.java)!!
            Chat.TYPE.GROUP -> chat.toObject(GroupChat::class.java)!!
        }
    }


    override suspend fun createPrivateChat(
        otherUserEmail: String,
        currentUser: User
    ): PrivateChat? {
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

        updateUserChatsList(otherUserEmail, otherUser.collection, chatDocument.id)

        chatDocument.set(privateChat).await()

        messagesDocument.set(Messages()).await()

        return chatDocument.get().await().toObject(PrivateChat::class.java)
    }

    override suspend fun createGroupChat(newGroupChat: NewGroupChat): GroupChat {
        val chatDocument = mFirestore.collection(Collections.CHATS)
            .document()

        // Create Messages Document to save it's id in the PrivateChat object if user is not null
        val messagesDocument = mFirestore.collection(Collections.MESSAGES)
            .document()

        val groupChat = GroupChat(
            cid = chatDocument.id,
            messagesId = messagesDocument.id,
            title = newGroupChat.title,
            membersCount = 1,
            description = newGroupChat.description,
            category = newGroupChat.category,
            photo = newGroupChat.photo,
            membersIds = listOf(newGroupChat.currentUser.email),
            admins = mutableListOf(newGroupChat.currentUser)
        )

        chatDocument.set(groupChat).await()
        messagesDocument.set(Messages()).await()

        updateUserChatsList(newGroupChat.currentUser.email, newGroupChat.currentUser.collection, chatDocument.id)

        return groupChat
    }


    override suspend fun updateUserChatsList(
        userEmail: String,
        userCollection: String,
        chatId: String
    ) {
        mFirestore.collection(userCollection)
            .document(userEmail)
            .update(UPDATES.CHATS_LIST, FieldValue.arrayUnion(chatId))
            .await()
    }


    override fun listenToChatsChanges(
        listener: EventListener<QuerySnapshot>
    ): ListenerRegistration {
        return mFirestore.collection(Collections.CHATS)
            .addSnapshotListener(listener)
    }

    override suspend fun listenToChatMessages(
        messagesId: String,
        onUpdate: suspend (messages: Messages) -> Unit
    ) {
        mFirestore.collection(Collections.MESSAGES)
            .document(messagesId)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    value?.toObject(Messages::class.java)?.let {
                        CoroutineScope(Dispatchers.Main).launch {
                            onUpdate(it)
                        }
                    }
                } else {
                    throw FirebaseFirestoreException(
                        "Something went wrong",
                        FirebaseFirestoreException.Code.NOT_FOUND
                    )
                }
            }
    }

    override suspend fun getRandomMessages(messagesId: String): List<Message> {
        return mFirestore.collection(Collections.MESSAGES)
            .document(messagesId)
            .get()
            .await()
            .toObject(Messages::class.java)
            ?.messages?.takeLast(3) ?: emptyList()

    }

    override fun getUserCommunities(user: User): Flow<List<GroupChat>> {
        return flow {
            val interestsChunked = user.interests.groupBy { it.title }
        }
    }

    override suspend fun getRandomCommunitiesBasedOnCategory(category: String): List<GroupChat> {
        val documents = mFirestore.collection(Collections.CHATS)
            .whereEqualTo("title", category)
            .orderBy("createdSince")
            .startAfter(lastVisibleCommunityDocument ?: 0)
            .limit(10)
            .get()
            .await()

        // For Paginating
        lastVisibleCommunityDocument = documents.documents[documents.size() - 1]

        return documents.toObjects(GroupChat::class.java).shuffled()
    }

    override suspend fun sendMessage(
        chatId: String,
        messagesId: String,
        message: Message
    ) {
        mFirestore.collection(Collections.MESSAGES)
            .document(messagesId)
            .update(Fields.MESSAGES, FieldValue.arrayUnion(message))
            .await()

        mFirestore.collection(Collections.CHATS)
            .document(chatId)
            .update(Fields.LAST_MESSAGE, message)
            .await()
    }

    override suspend fun getInterests(): List<Interest> {
        return mFirestore.collection(Collections.INTERESTS)
            .get()
            .await()
            .toObjects(Interest::class.java)
    }

    override suspend fun getTopics(): List<Topic> {
        return mFirestore.collection(Collections.TOPICS)
            .get()
            .await()
            .toObjects(Topic::class.java)
    }

    override fun getUserInterestsWithTopics(user: User): Flow<List<InterestWithTopics>> {
        val interestsChunks = user.interests.chunked(10)
        Log.d("NetworkDatabase", "interestsWithTopicsSize: ${interestsChunks.size}")
        return flow {
            interestsChunks.forEach { interestChunk ->
                val titles = interestChunk.map { interest -> interest.title }
                Log.d("UserInterests", "getUserInterestsWithTopics: $titles")
                mFirestore.collection(Collections.TOPICS)
                    .whereIn("category", titles)
                    .get()
                    .await()
                    .toObjects(Topic::class.java).also { topics ->
                        val interestsWithTopics = InterestWithTopics.create(interestChunk, topics)
                        emit(interestsWithTopics)
                    }
            }
        }
    }

    override suspend fun updateUser(user: User) {
        mFirestore.collection(user.collection)
            .document(user.email)
            .set(user)
            .await()
    }

    object Collections {
        const val USERS = "users"
        const val GOOGLE_USERS = "google_users"
        const val CHATS = "chats"
        const val MESSAGES = "messages"
        const val INTERESTS = "interests"
        const val TOPICS = "topics"

    }

    object UPDATES {
        const val CHATS_LIST = "chats_list"
    }
}
