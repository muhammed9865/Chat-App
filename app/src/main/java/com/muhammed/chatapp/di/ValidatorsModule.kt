package com.muhammed.chatapp.di

import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.domain.use_cases.ValidateNickname
import com.muhammed.chatapp.domain.use_cases.ValidatePassword
import com.muhammed.chatapp.domain.use_cases.ValidateRepeatedPassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ValidatorsModule {

    @Provides
    @Singleton
    fun provideEmailValidator(): ValidateEmail = ValidateEmail()

    @Provides
    @Singleton
    fun providePasswordValidator(): ValidatePassword = ValidatePassword()

    @Provides
    @Singleton
    fun provideRepeatedPasswordValidator(): ValidateRepeatedPassword = ValidateRepeatedPassword()

    @Provides
    @Singleton
    fun provideNicknameValidator(): ValidateNickname = ValidateNickname()
}