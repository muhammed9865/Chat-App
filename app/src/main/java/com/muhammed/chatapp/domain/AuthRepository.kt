package com.muhammed.chatapp.domain

import com.google.firebase.auth.FirebaseUser
import com.muhammed.chatapp.data.EmailAndPasswordAuth
import com.muhammed.chatapp.data.Firestore
import com.muhammed.chatapp.domain.validation.OperationResult
import com.muhammed.chatapp.pojo.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val mEmailAndPasswordAuth: EmailAndPasswordAuth,
    private val mFirestore: Firestore,
) {
    fun registerUser(
        nickname: String,
        email: String,
        password: String,
        onComplete: Callbacks.AuthCompleteListener
    ) {
        mEmailAndPasswordAuth.registerNewUser(email, password, object : Callbacks.AuthListener {
            override fun onSuccess(fUser: FirebaseUser?) {
                fUser?.let {
                    // Creating the user object to save it into Firestore.
                    val user = User(
                        uid = fUser.uid,
                        nickname = nickname,
                        email = email,
                        password = encodePassword(password)
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
        mEmailAndPasswordAuth.loginUser(email, password, object : Callbacks.AuthListener {
            override fun onSuccess(fUser: FirebaseUser?) {
                fUser?.let {
                    val user = User(
                        uid = fUser.uid,
                        nickname = fUser.displayName ?: "None",
                        email = email,
                        password = encodePassword(password)
                    )
                    onComplete.onSuccess(user)
                }
            }

            override fun onFailure(message: String) {
                onComplete.onFailure(message)
            }
        })

    }

    suspend fun saveUserOnFirestore(user: User): Flow<OperationResult> {
        val result = mFirestore.saveUser(user)
        return flow {
            result.collect()
        }
    }

    private fun encodePassword(password: String): String {
        return password.encodeToByteArray().toString()
    }
}