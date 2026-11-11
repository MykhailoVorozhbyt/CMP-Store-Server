package com.store.component.customer.data.mapper

import com.store.component.customer.model.CustomerProfileRequest
import com.store.core.data.database.customer.CartItemEntity
import com.store.core.data.database.customer.CustomerEntity
import com.store.core.data.database.customer.CustomerWithCart
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

internal fun CustomerWithCart.toCustomer(): Customer = Customer(
    id = customer.id,
    firstName = customer.firstName,
    lastName = customer.lastName,
    email = customer.email,
    city = customer.city,
    postalCode = customer.postalCode,
    address = customer.address,
    phoneNumber = phoneNumberOf(customer.phoneDialCode, customer.phoneNumber),
    cart = cart.map { it.toCartItem() },
    isAdmin = customer.isAdmin,
)

internal fun Customer.toEntity(): CustomerEntity = CustomerEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    city = city,
    postalCode = postalCode,
    address = address,
    phoneDialCode = phoneNumber?.dialCode,
    phoneNumber = phoneNumber?.number,
    isAdmin = isAdmin,
)

internal fun Customer.toCartEntities(): List<CartItemEntity> = cart.map { item ->
    CartItemEntity(
        id = item.id,
        customerId = id,
        productId = item.productId,
        flavor = item.flavor,
        quantity = item.quantity,
    )
}

internal fun Customer.withProfile(request: CustomerProfileRequest): Customer = copy(
    firstName = request.firstName,
    lastName = request.lastName,
    city = request.city,
    postalCode = request.postalCode,
    address = request.address,
    phoneNumber = request.phoneNumber,
)

private fun phoneNumberOf(dialCode: Int?, number: String?): PhoneNumber? =
    if (dialCode != null && number != null) PhoneNumber(dialCode = dialCode, number = number) else null

private fun CartItemEntity.toCartItem(): CartItem = CartItem(
    id = id,
    productId = productId,
    flavor = flavor,
    quantity = quantity,
)
