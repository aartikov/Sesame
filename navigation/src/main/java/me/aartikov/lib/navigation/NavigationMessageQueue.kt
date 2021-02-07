package me.aartikov.lib.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

class NavigationMessageQueue {
    private val channel = Channel<NavigationMessage>(Channel.UNLIMITED)
    val flow = channel.receiveAsFlow()

    fun send(message: NavigationMessage) {
        channel.offer(message)
    }
}

fun NavigationMessageQueue.bind(
    lifecycleOwner: LifecycleOwner,
    dispatcher: NavigationMessageDispatcher,
    node: Any
) {
    lifecycleOwner.lifecycleScope.launchWhenResumed {
        flow.collect { message ->
            dispatcher.dispatch(message, node)
        }
    }
}