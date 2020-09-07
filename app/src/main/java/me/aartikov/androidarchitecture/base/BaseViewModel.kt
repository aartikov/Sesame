package me.aartikov.androidarchitecture.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.aartikov.lib.data_binding.PropertyHost
import me.aartikov.lib.data_binding.command
import me.aartikov.lib.navigation.NavigationMessage

open class BaseViewModel : ViewModel(), PropertyHost {

    override val propertyHostScope get() = viewModelScope

    val navigationMessages = command<NavigationMessage>()
    val showErrorCommand = command<String>()

    protected fun navigate(navigationMessage: NavigationMessage) {
        navigationMessages.send(navigationMessage)
    }

    protected fun showError(e: Throwable) {
        showErrorCommand.send(e.message ?: "Error")
    }

    protected fun launchWithErrorHandling(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                showError(e)
            }
        }
    }
}