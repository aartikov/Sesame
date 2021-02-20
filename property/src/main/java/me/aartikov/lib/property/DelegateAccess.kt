package me.aartikov.lib.property

import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty0

internal object DelegateAccess {
    internal val delegate = ThreadLocal<Any?>()
    internal val delegateRequested = ThreadLocal<Boolean>().apply { set(false) }
}

internal val <T> KProperty0<T>.delegate: Any?
    get() {
        try {
            DelegateAccess.delegateRequested.set(true)
            this.get()
            return DelegateAccess.delegate.get()
        } finally {
            DelegateAccess.delegate.set(null)
            DelegateAccess.delegateRequested.set(false)
        }
    }

/**
 * Retrieves [StateFlow] from an observable property (See: [PropertyHost.state], [PropertyHost.stateFromFlow], [PropertyHost.computed]).
 */
@Suppress("UNCHECKED_CAST")
val <T> KProperty0<T>.flow: StateFlow<T>
    get() = delegate as? StateFlow<T>
        ?: throw IllegalArgumentException("Property $name is not observable. Make sure you use state, stateFromFlow or computed to create it.")