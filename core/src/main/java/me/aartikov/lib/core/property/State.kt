package me.aartikov.lib.core.property

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

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

fun <T> PropertyHost.state(initialValue: T): MutableStateDelegate<T> {
    return MutableStateDelegate(MutableStateFlow(initialValue))
}

fun <T> PropertyHost.stateFromFlow(initialValue: T, flow: Flow<T>): StateDelegate<T> {
    val resultFlow = MutableStateFlow(initialValue)
    propertyHostScope.launch {
        flow.collect {
            resultFlow.value = it
        }
    }
    return StateDelegate(resultFlow)
}

fun <T> PropertyHost.stateFromFlow(stateFlow: StateFlow<T>): StateDelegate<T> {
    return StateDelegate(stateFlow)
}

fun <T> PropertyHost.stateFromFlow(mutableStateFlow: MutableStateFlow<T>): MutableStateDelegate<T> {
    return MutableStateDelegate(mutableStateFlow)
}