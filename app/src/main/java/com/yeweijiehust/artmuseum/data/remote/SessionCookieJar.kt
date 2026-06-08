package com.yeweijiehust.artmuseum.data.remote

import com.yeweijiehust.artmuseum.data.preferences.AppPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionCookieJar @Inject constructor(private val preferences: AppPreferences) : CookieJar {
    @Volatile
    private var cookie: Cookie? = preferences.loadCookie()?.let(::parseCookie)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.firstOrNull { it.name == COOKIE_NAME }?.let {
            cookie = it
            preferences.saveCookie(it.toString())
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val current = cookie ?: return emptyList()
        if (current.expiresAt < System.currentTimeMillis()) {
            clear()
            return emptyList()
        }
        return if (current.matches(url)) listOf(current) else emptyList()
    }

    fun clear() {
        cookie = null
        preferences.saveCookie(null)
    }

    private fun parseCookie(raw: String): Cookie? = Cookie.parse(preferences.loadEndpoint().toHttpUrl(), raw)

    companion object {
        private const val COOKIE_NAME = "am_session"
    }
}
