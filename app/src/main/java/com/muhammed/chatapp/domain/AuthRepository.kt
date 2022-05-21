package com.muhammed.chatapp.domain

import com.google.firebase.auth.FirebaseUser
import com.muhammed.chatapp.data.Auth
import com.muhammed.chatapp.data.Firestore
import com.muhammed.chatapp.domain.validation.OperationResult
import com.muhammed.chatapp.pojo.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val mAuth: Auth,
    private val mFirestore: Firestore
) {
     fun registerUser(
        nickname: String,
        email: String,
        password: String,
        onComplete: Callbacks.AuthCompleteListener

    ){
        mAuth.registerNewUser(email, password, object : Callbacks.AuthListener {
            override fun onSuccess(fUser: FirebaseUser?) {
                // Creating the user object to save it into Firestore.
                if (fUser != null) {
                    val user = User(
                        uid = fUser.uid,
                        nickname = nickname,
                        email = email
                    )
                    mAuth.sendVerificationMessage(fUser, object : Callbacks.VerificationListener {
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


    suspend fun saveUserOnFirestore(user: User): Flow<OperationResult> {
        val result = mFirestore.saveUser(user)
        return flow {
            result.collect()
        }
    }
}