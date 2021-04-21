package me.aartikov.sesame.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

/**
 * Coordinates handling of [navigation messages][NavigationMessage].
 * There should be a single instance of this class per Activity.
 */
class NavigationMessageDispatcher(
    private val nodeWalker: NodeWalker = DefaultNodeWalker(),
    private val onError: ((NavigationError) -> Unit)? = null
) {
    private val resumed = MutableStateFlow(false)
    private val messageHandlingInProgress = MutableStateFlow(false)

    /**
     * Indicates that it is allowed to call [dispatch].
     * NavigationMessageHandler can handle messages only when an activity is in resumed state and no other message handling is in progress.
     */
    val canHandleMessages = combine(resumed, messageHandlingInProgress) { resumed, messageHandlingInProgress ->
        canHandleMessages(resumed, messageHandlingInProgress)
    }

    /**
     * Notifies that Activity and its fragments are resumed.
     * Should be called from Activity.onResumeFragments().
     */
    fun resume() {
        resumed.value = true
    }

    /**
     * Notifies that Activity is paused.
     * Should be called from Activity.onPause().
     */
    fun pause() {
        resumed.value = false
    }

    /**
     * Sends a [message] for processing.
     *
     * During the processing a message will be passed through a chain of nodes starting from the [firstNode]. [NodeWalker] helps to iterate nodes.
     * If a node implements [NavigationMessageHandler] a message will be passed to [NavigationMessageHandler.handleNavigationMessage].
     * If [NavigationMessageHandler.handleNavigationMessage] returns true (a message is handled) processing is stopped.
     * If there is no [NavigationMessageHandler] that handled a message than [onError] with [NavigationError.MessageIsNotHandled] will be called.
     *
     * This method can be called only when when value of [canHandleMessages] is true. Otherwise [NavigationError.DispatcherCantHandleMessages] will be called and a message will be dropped.
     * Use [NavigationMessageQueue.bind] to be sure that messages are dispatched correctly.
     */
    fun dispatch(message: NavigationMessage, firstNode: Any) {
        val canHandle = canHandleMessages(resumed.value, messageHandlingInProgress.value)
        if (!canHandle) {
            onError?.invoke(NavigationError.DispatcherCantHandleMessages(message))
            return
        }

        messageHandlingInProgress.value = true
        try {
            val handled = handle(message, firstNode)
            if (!handled) {
                onError?.invoke(NavigationError.MessageIsNotHandled(message))
            }
        } finally {
            messageHandlingInProgress.value = false
        }
    }

    private fun handle(message: NavigationMessage, firstNode: Any): Boolean {
        var node: Any? = firstNode
        do {
            if (node is NavigationMessageHandler && node.handleNavigationMessage(message)) {
                return true
            }
            node = node?.let { nodeWalker.getNextNode(it) }
        } while (node != null)

        return false
    }

    private fun canHandleMessages(dispatcherResumed: Boolean, messageHandlingInProgress: Boolean): Boolean {
        return dispatcherResumed && !messageHandlingInProgress
    }
}