package org.cmp.store.utils

import org.cmp.store.database.dao.AuthCredentialDao
import org.cmp.store.database.dao.CustomerDao
import org.cmp.store.domain.auth.AuthCredential
import org.cmp.store.domain.customer.Customer
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.sql.SQLException

/**
 * DAO decorators that reproduce a lost signup race.
 *
 * The race window cannot be reproduced from database state alone: `AuthService.authorizeManual`
 * guards its inserts with `findByEmail`, so any email that would collide is already rejected
 * earlier with ACCOUNT_HAS_NO_PASSWORD. Reaching the ExposedSQLException branch requires a
 * competing transaction to commit *between* that check and the insert — which is what these
 * stand in for.
 *
 * Each decorator overrides exactly one method and delegates the rest, so adding a method to
 * either DAO interface will not break them.
 */

/**
 * Raises the failure a lost race raises: a unique-constraint violation surfacing as
 * [ExposedSQLException]. Shared by both decorators so either half of the race — failing on
 * the first insert or on the second — reports it identically.
 */
private fun uniqueViolation(constraint: String): Nothing = throw ExposedSQLException(
    cause = SQLException("UNIQUE constraint failed: $constraint"),
    contexts = emptyList(),
    transaction = TransactionManager.current(),
)

/**
 * Fails the customer insert — the first of the two writes. Nothing is written at all, so this
 * pins down the error contract only; see [RacingCredentialDao] for the rollback half.
 */
internal class RacingCustomerDao(
    private val delegate: CustomerDao,
) : CustomerDao by delegate {
    override fun insertWithinTransaction(customer: Customer) =
        uniqueViolation("customers.email")
}

/**
 * Fails the credential insert — the *second* write — leaving a real customer row inside the
 * open transaction. That partial write is what makes a rollback assertion meaningful: with the
 * real credential DAO the race can only ever be lost on the first insert.
 */
internal class RacingCredentialDao(
    private val delegate: AuthCredentialDao,
) : AuthCredentialDao by delegate {
    override fun insertWithinTransaction(credential: AuthCredential) =
        uniqueViolation("auth_credentials.provider, auth_credentials.email")
}
