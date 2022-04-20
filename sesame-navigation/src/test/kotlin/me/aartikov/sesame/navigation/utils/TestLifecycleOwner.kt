package me.aartikov.sesame.navigation.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycleOwner : LifecycleOwner {

    private val lifecycle = LifecycleRegistry.createUnsafe(this)

    override fun getLifecycle() = lifecycle

    fun moveToState(state: Lifecycle.State) {
        lifecycle.currentState = state
    }
}