package me.aartikov.sesame.property

import kotlinx.coroutines.CoroutineScope

/**
 * Gives access to methods [state], [stateFromFlow], [computed], [autorun], [command]. Adds ability to send commands.
 * In MVVM architecture [PropertyHost] is View Model.
 */
interface PropertyHost {

    /**
     * A [CoroutineScope] where computed properties will work in.
     */
    val propertyHostScope: CoroutineScope

    /**
     * Sends a command.
     */
    operator fun <T> Command<T>.invoke(command: T) {
        send(command)
    }

    /**
     * Sends a command without arguments.
     */
    operator fun Command<Unit>.invoke() {
        send(Unit)
    }
}