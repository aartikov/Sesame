package me.aartikov.lib.data_binding

import kotlinx.coroutines.channels.Channel

class Command<T> internal constructor() {
    private val channel = Channel<T>(Channel.UNLIMITED)

    fun send(command: T) {
        channel.offer(command)
    }

    suspend fun receive(): T {
        return channel.receive()
    }
}

fun <T> command() = Command<T>()