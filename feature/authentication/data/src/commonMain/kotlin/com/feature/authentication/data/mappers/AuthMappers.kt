package com.feature.authentication.data.mappers

import com.feature.authentication.data.model.AuthRequestDto
import com.feature.authentication.data.model.AuthResponseDto
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
