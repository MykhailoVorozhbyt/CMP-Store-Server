package com.feature.authentication.presentation.di

import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.feature.authentication.domain.usecases.ReadCustomerUseCase
import com.feature.authentication.domain.usecases.SignInUseCase
import com.feature.authentication.domain.usecases.SignOutUseCase
import com.feature.authentication.domain.usecases.UpdateCustomerUseCase
import com.feature.authentication.presentation.AuthenticationScreen
import com.feature.authentication.presentation.AuthenticationViewModel
import com.feature.authentication.presentation.handler.SignInFailureHandler
import com.feature.authentication.presentation.handler.SignInFailureHandlerImpl
import com.feature.authentication.presentation.validator.AuthenticationValidator
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

    factoryOf(::SignInUseCase)
    factoryOf(::GetCurrentUserIdUseCase)
    factoryOf(::ReadCustomerUseCase)
    factoryOf(::UpdateCustomerUseCase)
    factoryOf(::SignOutUseCase)

    factoryOf(::SignInFailureHandlerImpl).bind(SignInFailureHandler::class)
    viewModelOf(::AuthenticationViewModel)

    navEntry(Screen.Auth.serializer()) {
        AuthenticationScreen()
    }
}
