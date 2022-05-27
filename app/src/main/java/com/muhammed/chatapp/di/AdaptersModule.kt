package com.muhammed.chatapp.di

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.domain.use_cases.ValidateCurrentUser
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.presentation.adapter.PrivateChatAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class AdaptersModule {

    /*@Provides
    fun provideOptions(mFirestore: FirebaseFirestore): FirestoreRecyclerOptions<PrivateChat> {
        val query = mFirestore.collection(FirestoreManager.Collections.CHATS).whereIn("cid")

        FirestoreRecyclerOptions.Builder<PrivateChat>()
            .setQuery()
            .build()
    }

    @Provides
    fun provideChatsAdapter(options: FirestoreRecyclerOptions<PrivateChat>): PrivateChatAdapter =
        PrivateChatAdapter(validateCurrentUser = ValidateCurrentUser(), options)*/


}