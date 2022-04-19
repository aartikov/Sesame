package me.aartikov.sesame.activable

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * Binds [Activable] to Android [Lifecycle]: onStart is translated to [Activable.onActive], onStop to [Activable.onInactive].
 */
fun Activable.bindToLifecycle(lifecycle: Lifecycle) {
    lifecycle.addObserver(ActivableLifecycleObserver(this))
}

private class ActivableLifecycleObserver(private val activable: Activable) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        activable.onActive()
    }

    override fun onStop(owner: LifecycleOwner) {
        activable.onInactive()
    }
}