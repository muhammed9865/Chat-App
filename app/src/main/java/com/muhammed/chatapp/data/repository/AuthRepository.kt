package com.muhammed.chatapp.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.muhammed.chatapp.data.implementation.network.EmailAndPasswordAuth
import com.muhammed.chatapp.data.implementation.network.FirestoreNetworkDatabaseImp
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.domain.Encoder
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Email and Password -> Firebase Authentication
// Google Authentication
class AuthRepository @Inject constructor(
    private val mEmailAndPasswordAuth: EmailAndPasswordAuth,
    private val mFirestoreNetworkDatabaseImp: FirestoreNetworkDatabaseImp,
    private val userRepository: UserRepository
) {
    suspend fun registerUser(
        nickname: String,
        email: String,
        password: String,
    ): User? {

        // Check if someone is already using the email on Google Auth
        val isRegisteredWithGoogleEmail = mFirestoreNetworkDatabaseImp.getGoogleUser(email) != null

        if (isRegisteredWithGoogleEmail) {
            throw Exception("Email is already in use")
        }

        // Registering the user with email and password on Firebase Authentication
        val fUser = mEmailAndPasswordAuth.registerNewUser(email, password).user


        fUser?.let {
            // Getting the token for notification
            val token = FirebaseMessaging.getInstance().token.await()

            // Creating the user object to save it into Firestore.
            val user = User(
                uid = fUser.uid,
                nickname = nickname,
                token = token,
                email = email,
                password = Encoder.encodePassword(password),
                collection = FirestoreNetworkDatabaseImp.Collections.USERS
            )

            saveUserOnFirestore(user)
            // Sending Verification message and then send the user to Viewmodel
            mEmailAndPasswordAuth.sendVerificationMessage(fUser)
            return user
        }
        return null
    }

    suspend fun resetPassword(email: String) = mEmailAndPasswordAuth.resetPassword(email)

    suspend fun loginUser(
        email: String,
        password: String
    ) {
        mEmailAndPasswordAuth.loginUserWithEmailAndPassword(email, password).user.also { fbUser ->
            if (fbUser != null) {
                if (fbUser.isEmailVerified) {
                    val user = userRepository.getUser(fbUser.email!!)
                    //  save user credentials to skip login the next time
                    userRepository.saveUserDetails(user)
                    return
                }
            }
            throw Exception("Either email or password is incorrect")
        }
    }


    suspend fun signOut() {
        mEmailAndPasswordAuth.signOut()
        userRepository.removeUser()
    }

    private suspend fun saveUserOnFirestore(user: User) =
        mFirestoreNetworkDatabaseImp.saveUser(user)


    suspend fun saveGoogleUser(user: User) = mFirestoreNetworkDatabaseImp.saveGoogleUser(user)

    suspend fun authenticateGoogleUser(uid: String) =
        mFirestoreNetworkDatabaseImp.getGoogleUser(uid)

}