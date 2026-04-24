package com.feature.authentication.domain.model

sealed interface CreateCustomerResult {
    data object Success : CreateCustomerResult
    data object UserAlreadyExists : CreateCustomerResult
    data class Failure(val errorCode: String) : CreateCustomerResult
}

inline fun CreateCustomerResult.onSuccess(action: (CreateCustomerResult.Success) -> Unit): CreateCustomerResult {
    if (this is CreateCustomerResult.Success) action(this)
    return this
}

inline fun CreateCustomerResult.onUserAlreadyExists(action: (CreateCustomerResult.UserAlreadyExists) -> Unit): CreateCustomerResult {
    if (this is CreateCustomerResult.UserAlreadyExists) action(this)
    return this
}

inline fun CreateCustomerResult.onFailure(action: (CreateCustomerResult.Failure) -> Unit): CreateCustomerResult {
    if (this is CreateCustomerResult.Failure) action(this)
    return this
}