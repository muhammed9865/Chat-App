package com.muhammed.chatapp.data.repository

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.data.pojo.chat.NewGroupChat
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.domain.use_cases.FilterUserChatRooms
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatsRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase,
    private val filterUserChatRooms: FilterUserChatRooms,
) {


    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        networkDatabase.createPrivateChat(otherUserEmail, currentUser = currentUser)


    fun listenToChatsChanges(
        user: User,
        onChange: (rooms: List<Chat>) -> Unit
    ): ListenerRegistration {
        return networkDatabase.listenToChatsChanges { roomsValue, roomsError ->
            if (roomsError == null) {
                roomsValue?.documents?.let { documents ->
                    val rooms = filterUserChatRooms.execute(documents, user)
                        .sortedByDescending { it.lastMessage.messageDate }

                    onChange(rooms)
                }
            } else {
                throw FirebaseFirestoreException(
                    "Something went wrong",
                    FirebaseFirestoreException.Code.DATA_LOSS
                )
            }
        }
    }

    suspend fun getChat(chatId: String, chatType: Chat.TYPE) =
        networkDatabase.getChat(chatId, chatType)

    suspend fun createGroupChat(newGroupChat: NewGroupChat): GroupChat {
        val group = networkDatabase.createGroupChat(newGroupChat)
        FirebaseMessaging.getInstance().subscribeToTopic(newGroupChat.category).await()

        return group
    }

    suspend fun joinGroup(groupChat: GroupChat, user: User) {
        // Updating user chat list with the group chat id
        val userChatList = user.chats_list.toMutableList()
        userChatList.add(groupChat.cid)
        val editedUser = user.copy(chats_list = userChatList)

        // Updating groupChat
        val groupMembersIds = groupChat.membersIds.toMutableList()
        groupMembersIds.add(user.email)
        groupChat.membersIds = groupMembersIds
        groupChat.membersCount++
        networkDatabase.joinCommunity(groupChat, editedUser)

        FirebaseMessaging.getInstance().subscribeToTopic(groupChat.category).await()
    }


    companion object {
        private const val TAG = "FirestoreRepository"
    }

}