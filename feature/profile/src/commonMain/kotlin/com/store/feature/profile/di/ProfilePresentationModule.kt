package com.store.feature.profile.di

import com.store.core.navigation.di.navEntry
import com.store.core.presentation.navigation.Screen
import com.store.feature.profile.ProfileScreen
import com.store.feature.profile.ProfileViewModel
import com.store.feature.profile.handler.ProfileMessageHandler
import com.store.feature.profile.handler.ProfileMessageHandlerImpl
import com.store.feature.profile.validator.ProfileValidator
import com.store.feature.profile.view_data.ProfileInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val profilePresentationModule = module {
    factoryOf(::ProfileValidator)
    factoryOf(::ProfileInitializer)
    factoryOf(::ProfileMessageHandlerImpl).bind(ProfileMessageHandler::class)
    viewModelOf(::ProfileViewModel)

    navEntry(Screen.Profile.serializer()) {
        ProfileScreen()
    }
}
