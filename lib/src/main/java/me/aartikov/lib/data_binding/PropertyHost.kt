package me.aartikov.lib.data_binding

import kotlinx.coroutines.CoroutineScope

interface PropertyHost {
    val propertyHostScope: CoroutineScope

    fun <T> Command<T>.send(command: T) {
        sendInternal(command)
    }
}