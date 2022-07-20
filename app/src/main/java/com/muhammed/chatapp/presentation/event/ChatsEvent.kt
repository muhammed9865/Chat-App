package com.muhammed.chatapp.presentation.event

import com.muhammed.chatapp.Filter
import com.muhammed.chatapp.data.pojo.chat.Chat
import com.muhammed.chatapp.data.pojo.chat.ChatAndRoom
import com.muhammed.chatapp.data.pojo.chat.GroupChat

sealed class ChatsEvent {
    object Idle : ChatsEvent()
    // should change the user isFirstTime status to false
    // Upon it many things won't show up
    object SetFirstTimeToFalse : ChatsEvent()
    object SignOut : ChatsEvent()

    data class CreatePrivateRoom(val email: String): ChatsEvent()
    data class JoinPrivateChat(val chatAndRoom: ChatAndRoom<Chat>): ChatsEvent()
    data class ShowGroupDetails(val group: GroupChat) : ChatsEvent()

    data class SearchChats(val query: String?) : ChatsEvent()

    // Concerned with CommunityFragment
    object EnteredCommunityFragment : ChatsEvent()
    object LoadUserCommunities : ChatsEvent()
    data class LoadRandomCommunitiesBasedOnFilter(val filter: Filter): ChatsEvent()
}