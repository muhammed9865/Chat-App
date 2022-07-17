package com.muhammed.chatapp.data.implementation.network

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuth @Inject constructor(private val client: GoogleSignInClient)
    {
    private var googleAuthCallback: GoogleAuthCallback.ViewModel? = null

    suspend fun signIn() {
        client.signOut().await()
        googleAuthCallback?.onSigningStart(client)
    }

    fun registerCallbackListener(listener: GoogleAuthCallback.ViewModel) {
        this.googleAuthCallback = listener
    }

    suspend fun onTaskResult(data: Intent) {
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
        suspend fun onSigningSuccess(client: GoogleSignInClient, account: GoogleSignInAccount)
        fun onSigningFailure(error: String?)
    }

}