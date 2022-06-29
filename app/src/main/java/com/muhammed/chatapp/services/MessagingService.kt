package com.muhammed.chatapp.services

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.implementation.network.FirestoreNetworkDatabaseImp
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService: FirebaseMessagingService() {
    @Inject
    lateinit var firestoreNetworkDatabaseImp: FirestoreNetworkDatabaseImp


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MessageService", "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("MessageReceived", "onMessageReceived: ${message.data}")
        message.notification?.let {
            showNotification(it.title ?: "None", it.body ?: "Null")
        }
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
        Log.d("Firebase", "onMessageSent: $msgId")
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.d("Firebase", "onSendError: ${exception.message.toString()}")
    }

    private fun showNotification(title: String, text: String) {
        val builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setContentTitle(title)
            .setContentTitle(text)
            .setSmallIcon(R.drawable.ic_chat_logo)
            .setColor(getColor(R.color.primaryColor))
            .build()

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder)
    }


}