package me.aartikov.lib.navigation

/**
 * Thrown when there is no [NavigationMessageHandler] to handle a [NavigationMessage].
 */
class NotHandledNavigationMessageException(message: NavigationMessage) :
    RuntimeException("There is no NavigationMessagesHandler to handle $message.")