package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.DataStoreManager
import com.muhammed.chatapp.data.EmailAndPasswordAuth
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.domain.Encoder
import com.muhammed.chatapp.pojo.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val mEmailAndPasswordAuth: EmailAndPasswordAuth,
    private val mFirestoreManager: FirestoreManager,
    private val dataStoreManager: DataStoreManager
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
    ) = mEmailAndPasswordAuth.loginUserWithEmailAndPassword(email, password).user

    val currentUserEmail = dataStoreManager.currentUserEmail
    val currentUserCategory = dataStoreManager.currentUserCategory
    val currentUserName = dataStoreManager.currentUserName

    suspend fun saveCurrentUserDetails(email: String, category: String, nickname: String) =
        dataStoreManager.saveCurrentUserDetails(email, category, nickname)


    fun getCurrentUser() = mEmailAndPasswordAuth.currentUser

    fun signOut() = mEmailAndPasswordAuth.signOut()

    private suspend fun saveUserOnFirestore(user: User) = mFirestoreManager.saveUser(user)


    suspend fun saveGoogleUser(user: User) = mFirestoreManager.saveGoogleUser(user)

    suspend fun authenticateGoogleUser(uid: String) = mFirestoreManager.getGoogleUser(uid)

}