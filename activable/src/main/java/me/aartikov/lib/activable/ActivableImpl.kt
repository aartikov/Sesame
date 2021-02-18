package me.aartikov.lib.activable

import kotlinx.coroutines.flow.MutableStateFlow

internal class ActivableImpl : Activable {

    override val activeFlow = MutableStateFlow(false)

    override fun onActive() {
        activeFlow.value = true
    }

    override fun onInactive() {
        activeFlow.value = false
    }
}