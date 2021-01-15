package me.aartikov.lib.data_binding

import kotlinx.coroutines.CoroutineScope

interface PropertyHost {
    val propertyHostScope: CoroutineScope

    operator fun <T> Command<T>.invoke(command: T) {
        send(command)
    }

    operator fun Command<Unit>.invoke() {
        send(Unit)
    }
}