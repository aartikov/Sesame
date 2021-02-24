package me.aartikov.lib.activable

import kotlinx.coroutines.flow.StateFlow

/**
 * A simple lifecycle with two states - active and inactive, and two callbacks - [onActive] and [onInactive].
 */
interface Activable {

    val activeFlow: StateFlow<Boolean>

    fun onActive()

    fun onInactive()
}

val Activable.active get() = activeFlow.value

fun Activable(): Activable = ActivableImpl()