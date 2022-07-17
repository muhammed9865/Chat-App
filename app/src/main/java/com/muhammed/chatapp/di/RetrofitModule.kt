package com.muhammed.chatapp.di

import com.muhammed.chatapp.BuildConfig
import com.muhammed.chatapp.data.implementation.network.NotificationsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    @Singleton
    fun provideClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient().newBuilder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(interceptor)
                addInterceptor {
                    it.proceed(it.request())
                }
            }
        }.build()

    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @Provides
    @Singleton
    fun provideNotificationsApi(retrofit: Retrofit): NotificationsApi =
        retrofit.create(NotificationsApi::class.java)

}