package me.aartikov.sesamecomposesample.app

import me.aartikov.sesamecomposesample.core.coreModule
import me.aartikov.sesamecomposesample.features.movies.moviesModule
import me.aartikov.sesamecomposesample.features.profile.profileModule

val allModules = listOf(
    coreModule,
    profileModule,
    moviesModule
)