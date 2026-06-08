package com.yeweijiehust.artmuseum.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yeweijiehust.artmuseum.domain.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("artmuseum_preferences")

@Singleton
class AppPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val endpointKey = stringPreferencesKey("endpoint")
    private val languageKey = stringPreferencesKey("language")
    private val cookieKey = stringPreferencesKey("cookie")

    val endpoint: Flow<String> = context.dataStore.data.map {
        it[endpointKey] ?: DEFAULT_ENDPOINT
    }

    val language: Flow<AppLanguage> = context.dataStore.data.map {
        it[languageKey]?.let(AppLanguage::valueOf) ?: AppLanguage.Device
    }

    suspend fun setEndpoint(endpoint: String) {
        context.dataStore.edit { it[endpointKey] = endpoint }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[languageKey] = language.name }
    }

    fun loadCookie(): String? = runBlocking { context.dataStore.data.first()[cookieKey] }

    fun saveCookie(cookie: String?) = runBlocking {
        context.dataStore.edit {
            if (cookie == null) it.remove(cookieKey) else it[cookieKey] = cookie
        }
    }

    companion object {
        const val DEFAULT_ENDPOINT = "https://artmuseum-w9mm.onrender.com"
    }
}
