package com.muhammed.chatapp.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.data.pojo.noification.NotificationModel
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var networkDatabase: NetworkDatabase

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var serializeEntityUseCase: SerializeEntityUseCase


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MessageService", "onNewToken: $token")

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.currentUser.collect {
                val user = it.copy(token = token)
                userRepository.updateUser(user)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("MessageReceived", "onMessageReceived: ${message.data}")
        message.data.let {
            val nm = NotificationModel.fromMap(it)
            val msg = serializeEntityUseCase.fromString<Message>(nm.body)
            showNotification(nm.title, msg)
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.e("MessagingService", "TASK REMOVED")

        val service = PendingIntent.getService(
            applicationContext,
            1001,
            Intent(applicationContext, MessagingService::class.java),
            PendingIntent.FLAG_ONE_SHOT
        )

        val am: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service)

    }


    private fun showNotification(title: String, message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            val currUser = userRepository.currentUser.first()
            if (message.sender.email != currUser.email) {
                val builder =
                    NotificationCompat.Builder(
                        this@MessagingService,
                        getString(R.string.default_notification_channel_id)
                    )
                        .setContentTitle(title)
                        .setContentText(message.text)
                        .setSmallIcon(R.drawable.ic_chat_logo)
                        .setColor(getColor(R.color.primaryColor))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setOnlyAlertOnce(false)
                        .build()


                withContext(Dispatchers.Main) {
                    val notificationManager: NotificationManagerCompat =
                        NotificationManagerCompat.from(this@MessagingService)
                    notificationManager.notify(1, builder)
                }
            }
        }
    }


}