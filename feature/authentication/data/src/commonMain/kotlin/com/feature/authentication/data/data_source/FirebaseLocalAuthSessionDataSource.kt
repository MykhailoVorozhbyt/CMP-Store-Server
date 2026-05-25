package com.feature.authentication.data.data_source

import com.store.core.security.SecureStorage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseLocalAuthSessionDataSource(secureStorage: SecureStorage) :
    DefaultLocalAuthSessionDataSource(secureStorage) {
    override suspend fun currentUserId(): String? =
        super.currentUserId() ?: Firebase.auth.currentUser?.uid

    override suspend fun signOut() {
        super.signOut()
        Firebase.auth.signOut()
    }
}