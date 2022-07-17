package com.muhammed.chatapp.data.pojo.noification

import android.util.Log
import com.google.gson.annotations.SerializedName

data class RequestNotification(
    @SerializedName("to")
    val token: String,
    @SerializedName("data")
    val notification: NotificationModel
) {
    init {
        Log.d("RequestNotification", ": $token")
    }
}
