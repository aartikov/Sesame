package me.aartikov.lib.data_binding

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class Command<T> internal constructor() {
    private val channel = Channel<T>(Channel.UNLIMITED)
    val flow = channel.receiveAsFlow()

    internal fun send(command: T) {
        channel.offer(command)
    }
}

fun <T> PropertyHost.command() = Command<T>()