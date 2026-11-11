package com.store.core.data.database.customer

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "customer")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String?,
    val postalCode: Int?,
    val address: String?,
    val phoneDialCode: Int?,
    val phoneNumber: String?,
    val isAdmin: Boolean,
)

@Entity(tableName = "cart_item", indices = [Index("customerId")])
data class CartItemEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val productId: String,
    val flavor: String?,
    val quantity: Int,
)

data class CustomerWithCart(
    @Embedded val customer: CustomerEntity,
    @Relation(parentColumn = "id", entityColumn = "customerId")
    val cart: List<CartItemEntity>,
)
