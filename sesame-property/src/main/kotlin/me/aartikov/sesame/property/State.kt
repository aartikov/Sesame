package me.aartikov.sesame.property

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

/**
 * Wraps [StateFlow] to a property delegate.
 * Use [PropertyHost.stateFromFlow] to create it.
 */
class StateDelegate<T> internal constructor(
    private val flow: StateFlow<T>
) : StateFlow<T> by flow {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (DelegateAccess.delegateRequested.get() == true) {
            DelegateAccess.delegate.set(this)
        }
        return flow.value
    }
}

/**
 * Wraps [MutableStateFlow] to a property delegate.
 * Use [PropertyHost.state] or [PropertyHost.stateFromFlow] to create it.
 */
class MutableStateDelegate<T> internal constructor(
    private val flow: MutableStateFlow<T>
) : MutableStateFlow<T> by flow {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (DelegateAccess.delegateRequested.get() == true) {
            DelegateAccess.delegate.set(this)
        }
        return flow.value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        flow.value = value
    }
}

/**
 * Creates an observable mutable property.
 * To observe value changes use [PropertyObserver.bind] or [PropertyHost.autorun].
 */
fun <T> PropertyHost.state(initialValue: T): MutableStateDelegate<T> {
    return MutableStateDelegate(MutableStateFlow(initialValue))
}

/**
 * Wraps [Flow] to an observable property.
 * To observe value changes use [PropertyObserver.bind] or [PropertyHost.autorun].
 */
fun <T> PropertyHost.stateFromFlow(initialValue: T, flow: Flow<T>): StateDelegate<T> {
    val resultFlow = MutableStateFlow(initialValue)
    propertyHostScope.launch {
        flow.collect {
            resultFlow.value = it
        }
    }
    return StateDelegate(resultFlow)
}

/**
 * Wraps [StateFlow] to an observable property.
 * To observe value changes use [PropertyObserver.bind] or [PropertyHost.autorun].
 */
fun <T> PropertyHost.stateFromFlow(stateFlow: StateFlow<T>): StateDelegate<T> {
    return StateDelegate(stateFlow)
}

/**
 * Wraps [MutableStateFlow] to an observable mutable property.
 * To observe value changes use [PropertyObserver.bind] or [PropertyHost.autorun].
 */
fun <T> PropertyHost.stateFromFlow(mutableStateFlow: MutableStateFlow<T>): MutableStateDelegate<T> {
    return MutableStateDelegate(mutableStateFlow)
}