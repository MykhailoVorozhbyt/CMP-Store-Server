package org.cmp.store.network

interface ApiError

enum class NetworkError(val message: String) : ApiError {
    USER_ALREADY_EXISTS("User already exists"),
    MISSING_CUSTOMER_ID("Missing customer id"),
    CUSTOMER_NOT_FOUND("Customer not found"),
    REQUEST_TIMEOUT("Request timeout"),
    UNAUTHORIZED("Unauthorized"),
    FORBIDDEN("Forbidden"),
    TOO_MANY_REQUESTS("Too many requests"),
    NO_INTERNET("No internet connection"),
    PAYLOAD_TOO_LARGE("Payload too large"),
    SERVER_ERROR("Server error"),
    SERIALIZATION("Serialization error"),
    UNKNOWN("Unknown error"),

    EMAIL_REQUIRED("Email required"),
    PASSWORD_REQUIRED("Password required"),
    PROVIDER_USER_ID_REQUIRED("Provider user id required"),
    INVALID_CREDENTIALS("Invalid credentials"),
    AUTH_PROVIDER_NOT_SUPPORTED("Auth provider not supported"),
    ACCOUNT_HAS_NO_PASSWORD("This account has no password. Sign in with Google"),
    INVALID_REFRESH_TOKEN("Invalid or expired refresh token"),
    TOKEN_REUSE_DETECTED("Token reuse detected. Sign in again"),
}