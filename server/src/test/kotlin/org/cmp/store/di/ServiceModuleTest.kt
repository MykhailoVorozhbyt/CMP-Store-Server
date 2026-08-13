package org.cmp.store.di

import org.cmp.store.database.dao.AuthCredentialDao
import org.cmp.store.database.dao.AuthSessionDao
import org.cmp.store.database.dao.CustomerDao
import org.cmp.store.database.dao.RefreshTokenDao
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

class ServiceModuleTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `serviceModule definitions are complete and resolvable`() {
        serviceModule.verify(
            extraTypes = listOf(
                CustomerDao::class,
                AuthCredentialDao::class,
                AuthSessionDao::class,
                RefreshTokenDao::class,
            )
        )
    }
}
