package com.feature.authentication.data.fakes

import com.feature.authentication.data.AuthSessionDataSource

class FakeAuthSessionDataSource : AuthSessionDataSource {
    var userId: String? = "test-uid"
    var signOutCalled = false

    override fun currentUserId(): String? = userId
    override suspend fun signOut() {
        signOutCalled = true
    }
}