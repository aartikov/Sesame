package me.aartikov.lib.navigation

/**
 * Iterates navigation nodes for message dispatching. See: [NavigationMessageDispatcher.dispatch].
 */
interface NodeWalker {
    /**
     * Returns next node.
     * @param node current node.
     * @return next node or null if there is no next node.
     */
    fun getNextNode(node: Any): Any?
}