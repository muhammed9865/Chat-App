package com.muhammed.chatapp.domain.use_cases

import com.muhammed.chatapp.data.repository.NetworkRepository
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(
    private val validateEmail: ValidateEmail,
    private val validateUserIsAlreadyFriend: ValidateUserIsAlreadyFriend,
    private val checkIfCurrentUserUseCase: CheckIfCurrentUserUseCase,
    private val networkRepository: NetworkRepository
) {

    suspend fun execute(otherUserEmail: String, currentUser: User, userChats: List<PrivateChat>): PrivateChat? {
        val emailResult = validateEmail.execute(otherUserEmail)
        if (!emailResult.isSuccessful) {
            throw Exception(emailResult.errorMessage.toString())
        }

        val isCurrentUser =
            checkIfCurrentUserUseCase.execute(currentUserEmail = currentUser.email, otherUserEmail)

        if (isCurrentUser.isSuccessful) {
            throw Exception("You can't add yourself")
        }

        val isUserAlreadyAdded = validateUserIsAlreadyFriend.execute(otherUserEmail, userChats)
        isUserAlreadyAdded?.let { throw Exception("User is already added") }

        return networkRepository.createNewPrivateChat(otherUserEmail, currentUser)
    }

}