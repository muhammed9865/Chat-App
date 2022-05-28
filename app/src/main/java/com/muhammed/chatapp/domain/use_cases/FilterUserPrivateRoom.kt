package com.muhammed.chatapp.domain.use_cases

import com.google.firebase.firestore.DocumentSnapshot
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User
import javax.inject.Inject

class FilterUserPrivateRoom @Inject constructor() {
    fun execute(documents: List<DocumentSnapshot>, user: User): List<PrivateChat> {
        val filteredDocuments =
            documents.filter { documentChange ->
               val document = documentChange.toObject(PrivateChat::class.java)
                document?.firstUser?.uid == user.uid || document?.secondUser?.uid == user.uid
            }
                .toSet()
        return filteredDocuments.mapNotNull { dc ->
            dc.toObject(PrivateChat::class.java)
        }

    }
}