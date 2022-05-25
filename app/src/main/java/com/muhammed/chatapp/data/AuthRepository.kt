package com.muhammed.chatapp.data

import com.google.firebase.auth.FirebaseUser
import com.muhammed.chatapp.domain.Encoder
import com.muhammed.chatapp.domain.OperationResult
import com.muhammed.chatapp.pojo.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val mEmailAndPasswordAuth: EmailAndPasswordAuth,
    private val mFirestoreManager: FirestoreManager,
) {
    fun registerUser(
        nickname: String,
        email: String,
        password: String,
        onComplete: Callbacks.AuthCompleteListener
    ) {
        mEmailAndPasswordAuth.registerNewUser(email, password, object : Callbacks.AuthListener {
            override fun onSuccess(fUser: FirebaseUser?, token: String?) {
                fUser?.let {
                    // Creating the user object to save it into Firestore.
                    val user = User(
                        uid = fUser.uid,
                        nickname = nickname,
                        email = email,
                        password = Encoder.encodePassword(password)
                    )
                    // Sending Verification message and then send the user to Viewmodel
                    mEmailAndPasswordAuth.sendVerificationMessage(
                        fUser,
                        object : Callbacks.VerificationListener {
                            override fun onSuccess() {
                                onComplete.onSuccess(user = user)
                            }

                            override fun onFailure(message: String) {
                                onComplete.onFailure(message)
                            }
                        })
                }
            }

            override fun onFailure(message: String) {
                onComplete.onFailure(message)
            }
        })

    }

    fun loginUser(
        email: String,
        password: String,
        onComplete: Callbacks.AuthCompleteListener
    ) {
        mEmailAndPasswordAuth.loginUserWithEmailAndPassword(email, password, object :
            Callbacks.AuthListener {
            override fun onSuccess(fUser: FirebaseUser?, token: String?) {
                fUser?.let {
                    val user = User(
                        uid = fUser.uid,
                        nickname = fUser.displayName ?: "None",
                        email = email,
                        password = Encoder.encodePassword(password)
                    )
                    onComplete.onSuccess(user, token = token)
                    }
                }


            override fun onFailure(message: String) {
                onComplete.onFailure(message)
            }
        })

    }

    fun getCurrentUser() = mEmailAndPasswordAuth.currentUser

    fun signOut() = mEmailAndPasswordAuth.signOut()

    suspend fun saveUserOnFirestore(user: User): Flow<OperationResult> {
        val result = mFirestoreManager.saveUser(user)
        return flow {
            result.collect()
        }
    }


    suspend fun saveUserOnFStore(user: User) = mFirestoreManager.saveUserWithoutReturn(user)

    suspend fun saveGoogleUser(user: User) = mFirestoreManager.saveGoogleUser(user)

    suspend fun authenticateGoogleUser(uid: String) = mFirestoreManager.getGoogleUser(uid)

}