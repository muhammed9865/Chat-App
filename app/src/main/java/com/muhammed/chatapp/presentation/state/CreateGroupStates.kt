package com.muhammed.chatapp.presentation.state

import android.graphics.Bitmap

sealed class CreateGroupStates {
    object Idle : CreateGroupStates()
    data class Failed(val error: String): CreateGroupStates()
    object Creating : CreateGroupStates()
    object CreatedSuccessfully : CreateGroupStates()
    data class ValidationState(
        val title: String = "",
        val titleError: String? = null,
        val desc: String = "",
        val descError: String? = null,
        val image: Bitmap? = null,
        val imageError: String? = null,
        val category: String = "",
        val categoryError: String? = null
    ): CreateGroupStates()

}