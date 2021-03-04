package me.aartikov.sesame.property

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A primitive for sending commands. See: [PropertyHost.command]
 */
class Command<T> internal constructor() {
    private val channel = Channel<T>(Channel.UNLIMITED)
    val flow = channel.receiveAsFlow()

    internal fun send(command: T) {
        channel.offer(command)
    }
}

/**
 * Creates a primitive for sending commands from [PropertyHost] to [PropertyObserver].
 * Use [PropertyHost.invoke] to send commands. Use [PropertyObserver.bind] to receive commands.
 */
fun <T> PropertyHost.command() = Command<T>()