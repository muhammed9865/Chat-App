package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.Filter
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.data.pojo.user.User
import javax.inject.Inject

class CommunityRepository @Inject constructor(private val networkDatabase: NetworkDatabase) {
    fun loadCommunitiesForUser(user: User) = networkDatabase.getUserCommunities(user)

    fun loadCommunitiesByInterest(filter: Filter, user: User) =
        networkDatabase.getRandomCommunitiesBasedOnCategory(filter.title, user)


    private fun fakeData(): List<GroupChat> {
        return mutableListOf<GroupChat>().also {
            it.addAll(
                listOf(
                    GroupChat(
                        cid = "1n4H1xQ0FLQLLfM5gojU",
                        messagesId = "681EeP5CACaEVt1XhGwr",
                        title = "Crypto For Tech",
                        category = "Crypto",
                        membersCount = 2000
                    ),
                    GroupChat(
                        title = "Fast Movie Star",
                        category = "Movies",
                        membersCount = 130620
                    ),
                    GroupChat(
                        title = "Intelligence Carbs",
                        category = "Art",
                        membersCount = 132
                    ),
                    GroupChat(
                        title = "Lead America",
                        category = "Finance",
                        membersCount = 15980
                    )

                )
            )
        }
    }

}