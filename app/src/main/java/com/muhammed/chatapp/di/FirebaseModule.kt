package com.muhammed.chatapp.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    fun provideStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    fun provideEmailAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // Specify signIn Options.
    @Provides
    @Singleton
    fun provideGoogleSignInOptions() =
        GoogleSignInOptions.Builder()
            .requestEmail()
            .requestId()
            .requestProfile()
            .build()

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context, options: GoogleSignInOptions) = GoogleSignIn.getClient(context, options)

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().also {
            val settings = firestoreSettings {
                isPersistenceEnabled = true
            }
            it.firestoreSettings = settings
        }
    }

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): DatabaseReference = Firebase.database.reference

}