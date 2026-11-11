package com.store.component.auth.data.data_source

import com.store.core.security.SecureStorage
import com.store.core.security.SessionScopedStorage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseLocalAuthSessionDataSource(
    secureStorage: SecureStorage,
    sessionScopedStorages: List<SessionScopedStorage>,
) : DefaultLocalAuthSessionDataSource(secureStorage, sessionScopedStorages) {
    override suspend fun signOut() {
        super.signOut()
        Firebase.auth.signOut()
    }
}
