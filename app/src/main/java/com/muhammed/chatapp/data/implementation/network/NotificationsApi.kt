package com.muhammed.chatapp.data.implementation.network

import com.muhammed.chatapp.SECRET_KEYS
import com.muhammed.chatapp.data.pojo.noification.RequestNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotificationsApi {
    @Headers(
        "Authorization: key=${SECRET_KEYS.FCM_SERVER_KEY}",
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("fcm/send")
    fun sendChatNotification(
        @Body requestNotification: RequestNotification
    ): Call<RequestNotification>
}