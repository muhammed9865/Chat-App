package com.muhammed.chatapp.data.pojo.noification

data class NotificationModel(
    val title: String,
    val body: String
) {
    fun serializeBody() {
        // TODO Implement
    }

    companion object {
        fun fromMap(map: Map<String, String>): NotificationModel {
            val title = map["title"] ?: ""
            val body = map["body"] ?: ""
            return NotificationModel(title, body)
        }
    }
}