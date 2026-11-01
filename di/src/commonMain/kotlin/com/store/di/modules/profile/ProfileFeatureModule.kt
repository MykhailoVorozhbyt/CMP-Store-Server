package com.store.di.modules.profile

import com.store.feature.profile.di.profilePresentationModule
import org.koin.dsl.module

val profileFeatureModule = module {
    includes(profilePresentationModule)
}
