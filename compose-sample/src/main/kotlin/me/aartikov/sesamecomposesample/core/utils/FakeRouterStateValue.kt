package me.aartikov.sesamecomposesample.core.utils

import com.arkivanov.decompose.router.RouterState

fun <T : Any> createFakeRouterState(instance: T): RouterState<*, T> {
    return RouterState("<fake>", instance)
}