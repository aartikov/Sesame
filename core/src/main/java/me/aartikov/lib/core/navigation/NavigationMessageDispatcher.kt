package me.aartikov.lib.core.navigation

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

open class NavigationMessageDispatcher(private val errorHandler: ((Exception) -> Unit)? = null) {

    private val messageChannel = Channel<Pair<NavigationMessage, Any>>(Channel.UNLIMITED)

    fun attach(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launchWhenResumed {
            messageChannel.receiveAsFlow().collect { (message, firstNode) ->
                process(message, firstNode)
            }
        }
    }

    fun dispatch(message: NavigationMessage, firstNode: Any) {
        messageChannel.offer(Pair(message, firstNode))
    }

    private fun process(message: NavigationMessage, firstNode: Any) {
        var node: Any? = firstNode
        do {
            if (node is NavigationMessageHandler && node.handleNavigationMessage(message)) {
                return
            }
            node = node?.let { getParentNode(it) }
        } while (node != null)

        if (errorHandler != null) {
            errorHandler.invoke(NotHandledNavigationMessageException())
        } else {
            throw NotHandledNavigationMessageException()
        }
    }

    protected open fun getParentNode(node: Any): Any? {
        return if (node is Fragment) {
            node.parentFragment ?: node.activity
        } else {
            null
        }
    }
}