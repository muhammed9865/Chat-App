package com.muhammed.chatapp.domain

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    private val googleOptions: GoogleSignInOptions,
    private val client: GoogleSignInClient
) {
     fun registerUser(
        nickname: String,
        email: String,
        password: String,
        onComplete: Callbacks.AuthCompleteListener
    ){
        mEmailAndPasswordAuth.registerNewUser(email, password, object : Callbacks.AuthListener {
            override fun onSuccess(fUser: FirebaseUser?) {
                fUser?.let{
                    // Creating the user object to save it into Firestore.
                    val user = User(
                        uid = fUser.uid,
                        nickname = nickname,
                        email = email,
                    )
                    // Sending Verification message and then send the user to Viewmodel
                    mEmailAndPasswordAuth.sendVerificationMessage(fUser, object : Callbacks.VerificationListener {
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

     fun signInWithGoogle() {

     }

    suspend fun saveUserOnFirestore(user: User): Flow<OperationResult> {
        val result = mFirestore.saveUser(user)
        return flow {
            result.collect()
        }
    }
}