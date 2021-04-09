package me.aartikov.sesame.navigation

sealed class NavigationError {

    /**
     * Indicates that there is no [NavigationMessageHandler] to handle a [NavigationMessage].
     */
    data class MessageHandlerMissing(val message: NavigationMessage) : NavigationError()

    /**
     * Indicates that [NavigationMessageDispatcher] can't handle messages but [NavigationMessageDispatcher.dispatch] was called.
     */
    data class DispatcherCantHandleMessages(val message: NavigationMessage) : NavigationError()
}