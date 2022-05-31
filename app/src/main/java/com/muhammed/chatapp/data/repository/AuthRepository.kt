package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.EmailAndPasswordAuth
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.domain.Encoder
import com.muhammed.chatapp.data.pojo.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val mEmailAndPasswordAuth: EmailAndPasswordAuth,
    private val mFirestoreManager: FirestoreManager,
) {
    suspend fun registerUser(
        nickname: String,
        email: String,
        password: String,
    ): User? {

        // Check if someone is already using the email on Google Auth
        val isRegisteredWithGoogleEmail = mFirestoreManager.getGoogleUser(email) != null

        if (isRegisteredWithGoogleEmail) {
            throw Exception("Email is already in use")
        }

        val fUser = mEmailAndPasswordAuth.registerNewUser(email, password).user
        fUser?.let {
            // Creating the user object to save it into Firestore.
            val user = User(
                uid = fUser.uid,
                nickname = nickname,
                email = email,
                password = Encoder.encodePassword(password),
                collection = FirestoreManager.Collections.USERS
            )
            saveUserOnFirestore(user)
            // Sending Verification message and then send the user to Viewmodel
            mEmailAndPasswordAuth.sendVerificationMessage(fUser)
            return user
        }
        return null
    }

    suspend fun loginUser(
        email: String,
        password: String
    ) = mEmailAndPasswordAuth.loginUserWithEmailAndPassword(email, password).user


    fun getCurrentUser() = mEmailAndPasswordAuth.currentUser

    fun signOut() = mEmailAndPasswordAuth.signOut()

    private suspend fun saveUserOnFirestore(user: User) = mFirestoreManager.saveUser(user)


    suspend fun saveGoogleUser(user: User) = mFirestoreManager.saveGoogleUser(user)

    suspend fun authenticateGoogleUser(uid: String) = mFirestoreManager.getGoogleUser(uid)

}