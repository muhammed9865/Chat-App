package com.muhammed.chatapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.chatapp.domain.validation.OperationResult
import com.muhammed.chatapp.pojo.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Firestore @Inject constructor(private val mFirestore: FirebaseFirestore) {
    suspend fun saveUser(user: User): Flow<OperationResult> {
        var flow: Flow<OperationResult> = emptyFlow()

        mFirestore.collection(Collections.USERS)
            .document(user.uid)
            .set(
               user
            )
            .addOnSuccessListener {
                flow = flow {
                    emit(OperationResult(
                        isSuccessful = true
                    ))
                }
            }
            .addOnFailureListener {
                flow = flow {
                    emit(
                        OperationResult(
                        isSuccessful = false,
                        errorMessage = it.message
                    )
                    )
                }
            }

        return flow
    }

    class Collections {
        companion object {
            const val USERS = "users"
            const val CHATS = "chats"
        }
    }
}