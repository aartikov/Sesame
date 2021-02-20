package me.aartikov.lib.property

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.reflect.KProperty0

/**
 * Allows to observe properties (see: [state] and [computed]) and to receive commands (see: [command]).
 * In MVVM architecture [PropertyObserver] is View.
 */
interface PropertyObserver {

    val propertyObserverLifecycleOwner: LifecycleOwner

    /**
     * Observes a [Flow]. Receives data only when a lifecycle is in STARTED state.
     */
    infix fun <T> Flow<T>.bind(consumer: (T) -> Unit) {
        propertyObserverLifecycleOwner.lifecycleScope.launchWhenStarted {
            this@bind.collect {
                consumer(it)
            }
        }
    }

    /**
     * Observes an observable property (See: [PropertyHost.state], [PropertyHost.stateFromFlow], [PropertyHost.computed]). Receives data only when a lifecycle is in STARTED state.
     */
    infix fun <T> KProperty0<T>.bind(consumer: (T) -> Unit) {
        this.flow bind consumer
    }

    /**
     * Receives commands (See: [PropertyHost.command]). Receives commands only when a lifecycle is in STARTED state.
     */
    infix fun <T> Command<T>.bind(consumer: (T) -> Unit) {
        this.flow bind consumer
    }
}