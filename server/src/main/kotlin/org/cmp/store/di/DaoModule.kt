package org.cmp.store.di

import org.cmp.store.database.dao.AuthCredentialDao
import org.cmp.store.database.dao.AuthCredentialDaoImpl
import org.cmp.store.database.dao.AuthSessionDao
import org.cmp.store.database.dao.AuthSessionDaoImpl
import org.cmp.store.database.dao.CustomerDao
import org.cmp.store.database.dao.CustomerDaoImpl
import org.cmp.store.database.dao.ProductDao
import org.cmp.store.database.dao.ProductDaoImpl
import org.cmp.store.database.dao.RefreshTokenDao
import org.cmp.store.database.dao.RefreshTokenDaoImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val daoModule = module {
    singleOf(::CustomerDaoImpl) bind CustomerDao::class
    singleOf(::AuthCredentialDaoImpl) bind AuthCredentialDao::class
    singleOf(::AuthSessionDaoImpl) bind AuthSessionDao::class
    singleOf(::RefreshTokenDaoImpl) bind RefreshTokenDao::class
    singleOf(::ProductDaoImpl) bind ProductDao::class
}