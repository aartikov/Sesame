package me.aartikov.lib.activable

import kotlinx.coroutines.flow.StateFlow

interface Activable {

    val activeFlow: StateFlow<Boolean>

    fun onActive()

    fun onInactive()
}

val Activable.active get() = activeFlow.value

fun Activable(): Activable = ActivableImpl()