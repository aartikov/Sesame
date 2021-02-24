package me.aartikov.androidarchitecture.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import me.aartikov.lib.activable.Activable
import me.aartikov.lib.activable.activableFlow
import me.aartikov.lib.navigation.NavigationMessage
import me.aartikov.lib.navigation.NavigationMessageQueue
import me.aartikov.lib.property.PropertyHost
import me.aartikov.lib.property.command

abstract class BaseViewModel : ViewModel(), PropertyHost, Activable by Activable() {

    override val propertyHostScope get() = viewModelScope

    val navigationMessageQueue = NavigationMessageQueue()
    val showError = command<String>()

    protected fun navigate(message: NavigationMessage) {
        navigationMessageQueue.send(message)
    }

    protected fun showError(e: Throwable) {
        showError(e.message ?: "Error")
    }

    protected fun <T> Flow<T>.toActivableFlow(): Flow<T> {
        return activableFlow(
            originalFlow = this,
            activable = this@BaseViewModel,
            scope = viewModelScope
        )
    }
}