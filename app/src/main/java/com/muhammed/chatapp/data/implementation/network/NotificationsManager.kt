package com.muhammed.chatapp.data.implementation.network

import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.data.pojo.noification.NotificationModel
import com.muhammed.chatapp.data.pojo.noification.RequestNotification
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NotificationsManager @Inject constructor(
    private val notificationsApi: NotificationsApi,
    private val serializeEntityUseCase: SerializeEntityUseCase
) {

    suspend fun sendNotificationTo(sendToToken: String, message: Message) {
        val notificationModel = NotificationModel(
            title = message.sender.nickname,
            body = serializeEntityUseCase.toString(message)
        )

        val requestNotification = RequestNotification(
            token = sendToToken,
            notification = notificationModel
        )

        notificationsApi.sendChatNotification(requestNotification)
            .enqueue(object : Callback<RequestNotification> {
                override fun onResponse(
                    call: Call<RequestNotification>,
                    response: Response<RequestNotification>
                ) {
                }

                override fun onFailure(call: Call<RequestNotification>, t: Throwable) {
                }
            })
    }
}