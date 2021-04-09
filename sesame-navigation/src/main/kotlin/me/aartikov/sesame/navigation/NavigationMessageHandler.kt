package me.aartikov.sesame.navigation

/**
 * Should be implemented by a navigation node to handle [navigation messages][NavigationMessage].
 * See more about message dispatching: [NavigationMessageDispatcher.dispatch].
 */
interface NavigationMessageHandler {

    /**
     * Handles a [NavigationMessage].
     * @param message a navigation message.
     * @return true if a [message] was handled, false otherwise.
     */
    fun handleNavigationMessage(message: NavigationMessage): Boolean
}