package me.aartikov.sesamecomposesample.utils

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

fun <T : Any> createFakeRouterStateValue(instance: T): Value<RouterState<*, T>> {
    return MutableValue(
        RouterState(
            activeChild = Child.Created(
                configuration = "<fake>",
                instance = instance
            )
        )
    )
}