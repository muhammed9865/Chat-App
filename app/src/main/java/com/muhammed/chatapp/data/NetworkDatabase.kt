package com.muhammed.chatapp.data

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
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
import kotlinx.coroutines.flow.Flow

interface NetworkDatabase {
    suspend fun saveUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun saveGoogleUser(user: User)

    @Throws(NullPointerException::class)
    suspend fun getUser(email: String): User

    suspend fun getGoogleUser(email: String): User?

    @Throws(NullPointerException::class)
    suspend fun getChat(chatId: String, chatType: Chat.TYPE): Chat

    // General Interests. Should be static on database
    suspend fun getInterests(): List<Interest>

    // General Topics. Should be static on database
    suspend fun getTopics(): List<Topic>

    fun getUserInterestsWithTopics(user: User): Flow<List<InterestWithTopics>>

    fun getUserCommunities(user: User): Flow<List<GroupChat>>

    @Throws(NetworkExceptions.NoCommunitiesFoundException::class)
    fun getRandomCommunitiesBasedOnCategory(category: String, user: User): Flow<List<GroupChat>>

    suspend fun createPrivateChat(otherUserEmail: String, currentUser: User): PrivateChat?

    suspend fun createGroupChat(newGroupChat: NewGroupChat): GroupChat

    suspend fun updateUserChatsList(userEmail: String, userCollection: String, chatId: String)

    fun listenToChatsChanges(listener: EventListener<QuerySnapshot>): ListenerRegistration

    suspend fun listenToChatMessages(
        messagesId: String,
        onUpdate: suspend (messages: Messages) -> Unit
    )

    fun cancelListeningToMessages()

    suspend fun joinCommunity(groupChat: GroupChat, user: User)

    suspend fun getRandomMessages(messagesId: String): List<Message>

    /*
    @param ChatId to set the last chat message to the @param message
    @param messagesId to add the message to the chat messages, should be a unique id to a unique messages document related to chatId
    */
    suspend fun sendMessage(chatId: String, messagesId: String, message: Message)
}