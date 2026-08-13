package org.cmp.store.di

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

class DaoModuleTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `daoModule definitions are complete and resolvable`() {
        daoModule.verify()
    }
}
