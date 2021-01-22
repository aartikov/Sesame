package me.aartikov.lib.property.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycleOwner : LifecycleOwner {

    private val lifecycle = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycle

    fun onCreate() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

    fun onStart() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

    fun onResume() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

    fun onPause() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

    fun onStop() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

    fun onDestroy() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
}