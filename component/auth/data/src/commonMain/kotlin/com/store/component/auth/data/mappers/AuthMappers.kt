package com.store.component.auth.data.mappers

import com.store.component.auth.data.model.AuthRequestDto
import com.store.component.auth.data.model.AuthResponseDto
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse

fun AuthRequest.toDto(): AuthRequestDto = AuthRequestDto(
    provider = provider,
    email = email,
    password = password,
    providerUserId = providerUserId,
    displayName = displayName,
    firstName = firstName,
    lastName = lastName,
)

fun AuthResponseDto.toAuthResponse(): AuthResponse = AuthResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    customer = customer.toCustomer(),
    isNewAccount = isNewAccount,
    provider = provider,
)
