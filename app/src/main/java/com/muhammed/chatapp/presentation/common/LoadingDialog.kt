package com.muhammed.chatapp.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muhammed.chatapp.databinding.DialogLoadingBinding

class LoadingDialog : BaseDialog() {
    private lateinit var binding: DialogLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoadingBinding.inflate(layoutInflater)
        isCancelable = false

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }



    companion object {
        @Volatile
        private var INSTANCE: LoadingDialog? = null

        fun getInstance(): LoadingDialog {
            INSTANCE ?: synchronized(this) {
                INSTANCE = LoadingDialog().also {
                    INSTANCE = it
                }
            }

            return INSTANCE!!
        }
    }
}