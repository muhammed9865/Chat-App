package com.muhammed.chatapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.chatapp.pojo.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreManager @Inject constructor(private val mFirestore: FirebaseFirestore) {


    suspend fun saveUser(user: User) {
        mFirestore.collection(Collections.USERS)
            .document(user.uid)
            .set(user)
            .await()
    }

    suspend fun saveGoogleUser(user: User) {
        mFirestore.collection(Collections.GOOGLE_USERS)
            .document(user.uid)
            .set(user)
            .await()
    }



    suspend fun getGoogleUser(uid: String): User? {
        return mFirestore.collection(Collections.GOOGLE_USERS)
            .document(uid)
            .get()
            .await().toObject(User::class.java)
    }

    class Collections {
        companion object {
            const val USERS = "users"
            const val GOOGLE_USERS = "google_users"
            const val CHATS = "chats"
        }
    }
}