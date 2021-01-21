package me.aartikov.lib.core.navigation

/**
 * Thrown when there is no [NavigationMessageHandler] to handle the [navigation message][NavigationMessage].
 */
class NotHandledNavigationMessageException
    : RuntimeException("You have no NavigationMessagesHandler to handle the message. Forgot to add?")