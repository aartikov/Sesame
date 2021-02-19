package me.aartikov.lib.activable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

fun Activable.bindToLifecycle(lifecycle: Lifecycle) {
    lifecycle.addObserver(ActivableLifecycleObserver(this))
}

private class ActivableLifecycleObserver(private val activable: Activable) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        activable.onActive()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        activable.onInactive()
    }
}