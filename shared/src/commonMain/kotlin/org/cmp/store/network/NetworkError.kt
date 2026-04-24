package org.cmp.store.network

interface ApiError

enum class NetworkError(val message: String) : ApiError {
    USER_ALREADY_EXISTS("User already exists"),
    MISSING_CUSTOMER_ID("Missing customer id"),
    CUSTOMER_NOT_FOUND("Customer not found"),
    REQUEST_TIMEOUT("Request timeout"),
    UNAUTHORIZED("Unauthorized"),
    TOO_MANY_REQUESTS("Too many requests"),
    NO_INTERNET("No internet connection"),
    PAYLOAD_TOO_LARGE("Payload too large"),
    SERVER_ERROR("Server error"),
    SERIALIZATION("Serialization error"),
    UNKNOWN("Unknown error")
}