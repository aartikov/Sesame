package me.aartikov.sesame.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Coordinates handling of [navigation messages][NavigationMessage].
 * There should be a single instance of this class per Activity.
 */
class NavigationMessageDispatcher(
    private val nodeWalker: NodeWalker = DefaultNodeWalker()
) {

    private var messageChannel = Channel<Pair<NavigationMessage, Any>>(Channel.UNLIMITED)
    private var collectingJob: Job? = null

    /**
     * Attaches a dispatcher to Activity lifecycle. Required because navigation can be executed only when an activity is resumed.
     * If a dispatcher is attached in the second time all unprocessed messages will be dropped. Use [NavigationMessageQueue] to avoid this situation.
     */
    fun attach(lifecycleOwner: LifecycleOwner) {
        if (collectingJob != null) {
            collectingJob?.cancel()

            // drop old messages, because firstNodes for them belong to an old activity
            messageChannel.close()
            messageChannel = Channel(Channel.UNLIMITED)
        }

        collectingJob = lifecycleOwner.lifecycleScope.launchWhenResumed {
            messageChannel.receiveAsFlow().collect { (message, firstNode) ->
                process(message, firstNode)
            }
        }
    }

    /**
     * Sends a [message] for processing. If another message is processing already or an activity is not resumed a message is queued.
     * During the processing a message will be passed through a chain of nodes starting from the [firstNode]. [NodeWalker] helps to iterate nodes.
     * If a node implements [NavigationMessageHandler] a message will be passed to [NavigationMessageHandler.handleNavigationMessage].
     * If [NavigationMessageHandler.handleNavigationMessage] returns true (a message is handled) processing is stopped.
     * If there is no [NavigationMessageHandler] that handled a message than [NotHandledNavigationMessageException] will be thrown.
     */
    fun dispatch(message: NavigationMessage, firstNode: Any) {
        messageChannel.offer(Pair(message, firstNode))
    }

    private fun process(message: NavigationMessage, firstNode: Any) {
        var node: Any? = firstNode
        do {
            if (node is NavigationMessageHandler && node.handleNavigationMessage(message)) {
                return
            }
            node = node?.let { nodeWalker.getNextNode(it) }
        } while (node != null)

        throw NotHandledNavigationMessageException(message)
    }
}