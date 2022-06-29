package com.muhammed.chatapp.presentation.common

import android.os.Bundle
import androidx.fragment.app.DialogFragment

open class BaseDialog: DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}