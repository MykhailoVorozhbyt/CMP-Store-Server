package com.store.di

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

@OptIn(KoinExperimentalAPI::class)
class KoinGraphTest {

    @Test
    fun every_constructor_dependency_of_the_shared_graph_is_registered() {
        sharedAppModule.verify(
            extraTypes = listOf(HttpClientEngine::class, HttpClientConfig::class),
        )
    }
}
