package me.aartikov.lib.data_binding

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlin.reflect.KProperty0

interface PropertyObserver {

    val propertyObserverLifecycleOwner: LifecycleOwner

    infix fun <T> Flow<T>.bind(consumer: (T) -> Unit) {
        propertyObserverLifecycleOwner.lifecycleScope.launchWhenStarted {
            this@bind.collect {
                consumer(it)
            }
        }
    }

    infix fun <T> KProperty0<T>.bind(consumer: (T) -> Unit) {
        this.flow bind consumer
    }

    infix fun <T> Command<T>.bind(consumer: (T) -> Unit) {
        this.flow bind consumer
    }
}