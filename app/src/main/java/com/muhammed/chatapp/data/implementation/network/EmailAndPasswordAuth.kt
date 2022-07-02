package com.muhammed.chatapp.data.implementation.network


import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmailAndPasswordAuth @Inject constructor(private val mAuth: FirebaseAuth) {

    suspend fun registerNewUser(email: String, password: String): AuthResult{
        return mAuth.createUserWithEmailAndPassword(email, password).await()
    }

    val currentUser = mAuth.currentUser

    fun signOut() = mAuth.signOut()

    suspend fun loginUserWithEmailAndPassword(email: String, password: String): AuthResult {
        return mAuth.signInWithEmailAndPassword(email, password).await()

    }



    suspend fun sendVerificationMessage(user: FirebaseUser) {
        user.sendEmailVerification().await()
    }


}