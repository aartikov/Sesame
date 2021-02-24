package me.aartikov.lib.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Helps to send [navigation messages][NavigationMessage] from a View Model to [NavigationMessageDispatcher].
 */
class NavigationMessageQueue {
    private val channel = Channel<NavigationMessage>(Channel.UNLIMITED)
    val flow = channel.receiveAsFlow()

    /**
     * Sends navigation message.
     * Messages are never lost. If message can't be send immediately it will be queued.
     */
    fun send(message: NavigationMessage) {
        channel.offer(message)
    }
}

/**
 * Binds a queue to [NavigationMessageDispatcher]. Navigation messages sent to a bound queue will be passed to a dispatcher automatically.
 *
 * @param dispatcher [NavigationMessageDispatcher]
 * @param node a node from which message dispatching will be started (see: [NavigationMessageDispatcher.dispatch]). In most cases it should be Fragment/Activity that owns a View Model with the queue.
 * @param lifecycleOwner to provide a lifecycle of Fragment/Activity. Required because navigation can be executed only when Fragment/Activity is resumed.
 */
fun NavigationMessageQueue.bind(
    dispatcher: NavigationMessageDispatcher,
    node: Any,
    lifecycleOwner: LifecycleOwner
) {
    lifecycleOwner.lifecycleScope.launchWhenResumed {
        flow.collect { message ->
            dispatcher.dispatch(message, node)
        }
    }
}