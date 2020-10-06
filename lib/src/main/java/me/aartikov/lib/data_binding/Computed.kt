package me.aartikov.lib.data_binding

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty0

fun <T, R> PropertyHost.computed(
    property: KProperty0<T>,
    transform: (T) -> R
): StateDelegate<R> {
    val flow = property.flow
    val resultFlow = MutableStateFlow(transform(flow.value))
    propertyHostScope.launch {
        flow
            .drop(1)
            .collect {
                resultFlow.value = transform(it)
            }
    }
    return StateDelegate(resultFlow)
}

fun <T1, T2, R> PropertyHost.computed(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    transform: (T1, T2) -> R
): StateDelegate<R> {
    val flow1 = property1.flow
    val flow2 = property2.flow

    val pairFlow = MutableStateFlow(Pair(flow1.value, flow2.value))
    val resultFlow = MutableStateFlow(transform(flow1.value, flow2.value))

    propertyHostScope.launch {
        flow1
            .drop(1)
            .collect {
                pairFlow.value = pairFlow.value.copy(first = it)
            }
    }

    propertyHostScope.launch {
        flow2
            .drop(1)
            .collect {
                pairFlow.value = pairFlow.value.copy(second = it)
            }
    }

    propertyHostScope.launch {
        pairFlow
            .drop(1)
            .collect {
                resultFlow.value = transform(it.first, it.second)
            }
    }

    return StateDelegate(resultFlow)
}

fun <T1, T2, T3, R> PropertyHost.computed(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    transform: (T1, T2, T3) -> R
): StateDelegate<R> {
    val flow1 = property1.flow
    val flow2 = property2.flow
    val flow3 = property3.flow

    val tripleFlow = MutableStateFlow(Triple(flow1.value, flow2.value, flow3.value))
    val resultFlow = MutableStateFlow(transform(flow1.value, flow2.value, flow3.value))

    propertyHostScope.launch {
        flow1
            .drop(1)
            .collect {
                tripleFlow.value = tripleFlow.value.copy(first = it)
            }
    }

    propertyHostScope.launch {
        flow2
            .drop(1)
            .collect {
                tripleFlow.value = tripleFlow.value.copy(second = it)
            }
    }

    propertyHostScope.launch {
        flow3
            .drop(1)
            .collect {
                tripleFlow.value = tripleFlow.value.copy(third = it)
            }
    }

    propertyHostScope.launch {
        tripleFlow
            .drop(1)
            .collect {
                resultFlow.value = transform(it.first, it.second, it.third)
            }
    }

    return StateDelegate(resultFlow)
}