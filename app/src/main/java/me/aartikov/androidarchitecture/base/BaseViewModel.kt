package me.aartikov.androidarchitecture.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.aartikov.lib.data_binding.PropertyHost
import me.aartikov.lib.data_binding.command
import me.aartikov.lib.navigation.NavigationMessage
import me.aartikov.lib.widget.WidgetHost

open class BaseViewModel : ViewModel(), PropertyHost, WidgetHost {

    override val propertyHostScope get() = viewModelScope
    override val widgetHostScope get() = viewModelScope

    val navigationMessages = command<NavigationMessage>()
    val showErrorCommand = command<String>()

    protected fun navigate(navigationMessage: NavigationMessage) {
        navigationMessages.send(navigationMessage)
    }

    protected fun showError(e: Throwable) {
        showErrorCommand.send(e.message ?: "Error")
    }
}