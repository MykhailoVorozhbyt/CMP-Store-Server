package com.feature.authentication.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

interface AuthSessionDataSource {
    fun currentUserId(): String?
    suspend fun signOut()
}

class FirebaseAuthSessionDataSource : AuthSessionDataSource {
    override fun currentUserId(): String? = Firebase.auth.currentUser?.uid

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }
}
