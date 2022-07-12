package com.muhammed.chatapp.data.implementation.network

import android.graphics.Bitmap
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class StorageNetworkImp @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun saveImage(image: Bitmap): String {
        val storageRef = storage.reference

        val imageRef = storageRef.child("images/chatApp${image.height}.${image.width}.${System.currentTimeMillis()}")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val uploadedImage = imageRef.putBytes(data).await()

        return uploadedImage.metadata?.reference?.downloadUrl?.await().toString()
    }
}