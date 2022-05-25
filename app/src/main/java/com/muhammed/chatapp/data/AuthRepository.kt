package com.muhammed.chatapp.data

import com.muhammed.chatapp.domain.Encoder
import com.muhammed.chatapp.pojo.User
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
        val fUser = mEmailAndPasswordAuth.registerNewUser(email, password).user
        fUser?.let {
            // Creating the user object to save it into Firestore.
            val user = User(
                uid = fUser.uid,
                nickname = nickname,
                email = email,
                password = Encoder.encodePassword(password)
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
    ) = mEmailAndPasswordAuth.loginUserWithEmailAndPassword(email, password)


    fun getCurrentUser() = mEmailAndPasswordAuth.currentUser

    fun signOut() = mEmailAndPasswordAuth.signOut()

    suspend fun saveUserOnFirestore(user: User) = mFirestoreManager.saveUser(user)


    suspend fun saveGoogleUser(user: User) = mFirestoreManager.saveGoogleUser(user)

    suspend fun authenticateGoogleUser(uid: String) = mFirestoreManager.getGoogleUser(uid)

}