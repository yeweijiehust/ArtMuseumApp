package com.yeweijiehust.artmuseum.di

import android.content.Context
import androidx.room.Room
import com.yeweijiehust.artmuseum.BuildConfig
import com.yeweijiehust.artmuseum.data.local.ArtMuseumDatabase
import com.yeweijiehust.artmuseum.data.local.ImageDao
import com.yeweijiehust.artmuseum.data.preferences.AppPreferences
import com.yeweijiehust.artmuseum.data.remote.ArtMuseumApi
import com.yeweijiehust.artmuseum.data.remote.SessionCookieJar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun okHttp(cookieJar: SessionCookieJar): OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
        }
        .build()

    @Provides
    @Singleton
    fun api(json: Json, client: OkHttpClient): ArtMuseumApi = Retrofit.Builder()
        .baseUrl(AppPreferences.DEFAULT_ENDPOINT)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ArtMuseumApi::class.java)

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): ArtMuseumDatabase =
        Room.databaseBuilder(context, ArtMuseumDatabase::class.java, "artmuseum.db").build()

    @Provides
    fun imageDao(database: ArtMuseumDatabase): ImageDao = database.imageDao()
}
