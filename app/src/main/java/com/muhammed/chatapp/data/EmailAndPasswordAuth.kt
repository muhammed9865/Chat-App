package com.muhammed.chatapp.data


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    fun signOut() = mAuth.signOut()

    fun loginUserWithEmailAndPassword(email: String, password: String, authListener: Callbacks.AuthListener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            authListener.onSuccess(it.user)
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