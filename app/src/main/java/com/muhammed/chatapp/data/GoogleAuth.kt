package com.muhammed.chatapp.data

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject

class GoogleAuth @Inject constructor(private val client: GoogleSignInClient)
    {
    private var googleAuthCallback: GoogleAuthCallback.ViewModel? = null
    fun signIn() {
        googleAuthCallback?.onSigningStart(client)
    }

    fun registerCallbackListener(listener: GoogleAuthCallback.ViewModel) {
        this.googleAuthCallback = listener
    }

    fun onTaskResult(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            googleAuthCallback?.onSigningSuccess(client, account)
        } catch (e: ApiException) {
            googleAuthCallback?.onSigningFailure(e.message)
        }
    }
}

interface GoogleAuthCallback {
    interface ViewModel {
        fun onSigningStart(client: GoogleSignInClient)
        fun onSigningSuccess(client: GoogleSignInClient, account: GoogleSignInAccount)
        fun onSigningFailure(error: String?)
    }

}