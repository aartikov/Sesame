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

@Suppress("UNCHECKED_CAST")
val <T> KProperty0<T>.flow: StateFlow<T>
    get() = delegate as StateFlow<T>