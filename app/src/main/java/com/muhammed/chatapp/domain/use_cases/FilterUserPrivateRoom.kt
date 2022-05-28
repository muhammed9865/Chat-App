package com.muhammed.chatapp.domain.use_cases

import com.google.firebase.firestore.DocumentSnapshot
import com.muhammed.chatapp.pojo.PrivateChat
import javax.inject.Inject

class FilterUserPrivateRoom @Inject constructor() {
    fun execute(documents: List<DocumentSnapshot>, chatIds: List<String>): List<PrivateChat> {
        val filteredDocuments =
            documents.filter { documentChange ->
                documentChange.toObject(
                    PrivateChat::class.java
                )?.cid in chatIds
            }
                .toSet()
        return filteredDocuments.mapNotNull { dc ->
            dc.toObject(PrivateChat::class.java)
        }

    }
}