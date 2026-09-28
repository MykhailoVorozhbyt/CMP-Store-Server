package com.store.feature.authentication.di

import com.store.feature.authentication.AuthenticationScreen
import com.store.feature.authentication.AuthenticationViewModel
import com.store.feature.authentication.handler.SignInFailureHandler
import com.store.feature.authentication.handler.SignInFailureHandlerImpl
import com.store.feature.authentication.validator.AuthenticationValidator
import com.store.core.domain.model.validation.email.EmailDomainValidationConfig
import com.store.core.domain.model.validation.email.EmailDomainValidator
import com.store.core.navigation.di.navEntry
import com.store.core.presentation.validation.email.EmailFieldValidator
import com.store.core.presentation.validation.email.EmailPatternValidator
import com.store.core.presentation.validation.password.PasswordFieldValidator
import com.store.core.presentation.navigation.Screen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val authenticationPresentationModule = module {
    single<EmailDomainValidationConfig> {
        EmailDomainValidationConfig.Impl()
    }
    factoryOf(::EmailPatternValidator)
    factoryOf(::EmailDomainValidator)
    factoryOf(::EmailFieldValidator)
    factoryOf(::PasswordFieldValidator)
    factoryOf(::AuthenticationValidator)

    factoryOf(::SignInFailureHandlerImpl).bind(SignInFailureHandler::class)
    viewModelOf(::AuthenticationViewModel)

    navEntry(Screen.Auth.serializer()) {
        AuthenticationScreen()
    }
}
