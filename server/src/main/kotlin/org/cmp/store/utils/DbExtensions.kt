package org.cmp.store.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

/**
 * Runs [block] in a single new transaction on Dispatchers.IO, keeping blocking JDBC
 * work off the request threads. To compose multiple writes atomically, wrap the
 * non-suspend `insertWithinTransaction` DAO variants in one dbQuery call.
 */
suspend fun <T> dbQuery(block: suspend () -> T): T =
    withContext(Dispatchers.IO) { suspendTransaction { block() } }