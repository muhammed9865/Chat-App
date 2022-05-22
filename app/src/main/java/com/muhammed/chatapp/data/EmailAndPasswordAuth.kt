package com.muhammed.chatapp.data


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.muhammed.chatapp.domain.Callbacks
import javax.inject.Inject

class EmailAndPasswordAuth @Inject constructor(private val mAuth: FirebaseAuth) {

    fun registerNewUser(email: String, password: String, authListener: Callbacks.AuthListener){
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
           authListener.onSuccess(it.user)
        }.addOnFailureListener {
            authListener.onFailure(it.message!!)


        }


    }

    val currentUser = mAuth.currentUser

    fun loginUserWithEmailAndPassword(email: String, password: String, authListener: Callbacks.AuthListener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            authListener.onSuccess(it.user)
        }.addOnFailureListener {
            authListener.onFailure(it.message!!)
        }
    }

    fun loginUserWithToken(token: String, authListener: Callbacks.AuthListener) {
        mAuth.signInWithCustomToken(token).addOnSuccessListener {
            authListener.onSuccess(it.user, token)
        }.addOnFailureListener {
            authListener.onFailure(it.message!!)
        }
    }

    fun getUserToken(user: FirebaseUser, authListener: Callbacks.AuthListener) {
        user.getIdToken(false).addOnSuccessListener {
            authListener.onSuccess(fUser = user, token = it.token)
        }.addOnFailureListener {
            authListener.onFailure(it.message!!)
        }
    }

    fun sendVerificationMessage(user: FirebaseUser, verificationListener: Callbacks.VerificationListener) {
        user.sendEmailVerification().addOnSuccessListener {
          verificationListener.onSuccess()
        }.addOnFailureListener {
            verificationListener.onFailure(it.message!!)
        }
    }


}