package com.muhammed.chatapp.di

import com.muhammed.chatapp.domain.use_cases.ValidateCurrentUser
import com.muhammed.chatapp.presentation.adapter.ChatsAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class AdaptersModule {

    @Provides
    fun provideChatsAdapter() = ChatsAdapter(validateCurrentUser = ValidateCurrentUser())


}