package com.yeweijiehust.artmuseum.data.remote

import com.yeweijiehust.artmuseum.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStore @Inject constructor() {
    val user = MutableStateFlow<User?>(null)
}
