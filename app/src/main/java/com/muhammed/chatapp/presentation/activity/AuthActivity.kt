package com.muhammed.chatapp.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muhammed.chatapp.R
import com.muhammed.chatapp.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity(){
    private val binding: ActivityAuthBinding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.primaryColor)
    }



}