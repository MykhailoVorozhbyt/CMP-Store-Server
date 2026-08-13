package org.cmp.store.features.auth.mappers

import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import org.cmp.store.features.auth.dto.AuthRequestDto
import org.cmp.store.features.auth.dto.AuthResponseDto
import org.cmp.store.features.auth.dto.SessionTokensDto
import org.cmp.store.features.customer.mappers.toDto
import org.cmp.store.features.session.models.SessionTokens

fun AuthRequestDto.toAuthRequest(): AuthRequest = AuthRequest(
    provider = provider,
    email = email,
    password = password,
    providerUserId = providerUserId,
    displayName = displayName,
    firstName = firstName,
    lastName = lastName,
)

fun AuthResponse.toDto(): AuthResponseDto = AuthResponseDto(
    accessToken = accessToken,
    refreshToken = refreshToken,
    customer = customer.toDto(),
    isNewAccount = isNewAccount,
    provider = provider,
)

fun SessionTokens.toDto(): SessionTokensDto = SessionTokensDto(
    accessToken = accessToken,
    refreshToken = refreshToken,
)
