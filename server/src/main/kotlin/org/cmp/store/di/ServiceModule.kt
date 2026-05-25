package org.cmp.store.di

import org.cmp.store.features.auth.service.AuthService
import org.cmp.store.features.auth.service.AuthServiceImpl
import org.cmp.store.features.session.service.AuthSessionService
import org.cmp.store.features.session.service.AuthSessionServiceImpl
import org.cmp.store.features.session.service.RefreshGraceCache
import org.cmp.store.features.session.service.SessionCleanupJob
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::AuthServiceImpl) bind AuthService::class
    single { RefreshGraceCache() }
    singleOf(::AuthSessionServiceImpl) bind AuthSessionService::class
    singleOf(::SessionCleanupJob)
}
