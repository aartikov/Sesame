package me.aartikov.androidarchitecture.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.aartikov.lib.data_binding.PropertyHost
import me.aartikov.lib.data_binding.command
import me.aartikov.lib.navigation.NavigationMessage

open class BaseViewModel : ViewModel(), PropertyHost {

    override val propertyHostScope get() = viewModelScope

    val navigate = command<NavigationMessage>()
    val showError = command<String>()

    protected fun showError(e: Throwable) {
        showError(e.message ?: "Error")
    }
}