package com.muhammed.chatapp.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muhammed.chatapp.databinding.DialogLoadingBinding

class LoadingDialog private constructor(): BaseDialog() {
    private lateinit var binding: DialogLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoadingBinding.inflate(layoutInflater)
        isCancelable = false
        return binding.root
    }



    companion object {
        @Volatile
        private var INSTANCE: LoadingDialog? = null

        fun getInstance(): LoadingDialog {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = LoadingDialog()
                return INSTANCE!!
            }

        }
    }
}