package me.aartikov.androidarchitecture.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.aartikov.lib.navigation.NavigationMessage
import me.aartikov.lib.navigation.NavigationMessageQueue
import me.aartikov.lib.property.PropertyHost
import me.aartikov.lib.property.command

open class BaseViewModel : ViewModel(), PropertyHost {

    override val propertyHostScope get() = viewModelScope

    val navigationMessageQueue = NavigationMessageQueue()
    val showError = command<String>()

    protected fun navigate(message: NavigationMessage) {
        navigationMessageQueue.send(message)
    }

    protected fun showError(e: Throwable) {
        showError(e.message ?: "Error")
    }
}