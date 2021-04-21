package me.aartikov.sesamesample.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import me.aartikov.sesame.activable.Activable
import me.aartikov.sesame.activable.activableFlow
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageQueue
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.command
import me.aartikov.sesamesample.Back

abstract class BaseViewModel : ViewModel(), PropertyHost, Activable by Activable() {

    override val propertyHostScope get() = viewModelScope

    val navigationMessageQueue = NavigationMessageQueue()
    val showError = command<LocalizedString>()

    protected fun navigate(message: NavigationMessage) {
        navigationMessageQueue.send(message)
    }

    protected fun showError(e: Throwable) {
        showError(LocalizedString.raw(e.message ?: "Error"))
    }

    open fun onBackPressed() {
        navigate(Back)
    }

    protected fun <T> Flow<T>.toActivableFlow(): Flow<T> {
        return activableFlow(
            originalFlow = this,
            activable = this@BaseViewModel,
            scope = viewModelScope
        )
    }
}