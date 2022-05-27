package com.muhammed.chatapp.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.muhammed.chatapp.pojo.Message
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DatabaseManager @Inject constructor(
    private val database: DatabaseReference
) {

    fun startListeningTo(email: String, onNewMessage: (message: Message) -> Unit) {
        database.child(FirestoreManager.Collections.CHATS)
            .child(email)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let {
                        Log.d("Database", "onDataChange: $it")
                        val message = snapshot.getValue(Message::class.java)
                        if (message != null) {
                            onNewMessage(message)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Database", error.details)
                }
            })
    }


    suspend fun sendMessageToChatRoom(userEmail: String, message: Message) {
        database
            .child(FirestoreManager.Collections.CHATS)
            .child(userEmail)
            .push()
            .setValue(message)
            .await()
    }
}