package com.mbdrmod.delivery.di

import android.content.Context
import androidx.room.Room
import com.mbdrmod.delivery.data.local.DeliveryDao
import com.mbdrmod.delivery.data.local.DeliveryDatabase
import com.mbdrmod.delivery.data.local.DeliveryDatabase.Companion.MIGRATION_1_2
import com.mbdrmod.delivery.data.remote.WebhookService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DeliveryDatabase {
        return Room.databaseBuilder(
            context,
            DeliveryDatabase::class.java,
            "delivery_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideDeliveryDao(database: DeliveryDatabase): DeliveryDao {
        return database.deliveryDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://placeholder.com/") // Base URL is overridden in service calls
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWebhookService(retrofit: Retrofit): WebhookService {
        return retrofit.create(WebhookService::class.java)
    }
}
