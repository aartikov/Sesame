package me.aartikov.lib.data_binding

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combineTransform
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
    val resultFlow = MutableStateFlow(transform(flow1.value, flow2.value))
    var firstSkipped = false
    propertyHostScope.launch {
        combineTransform(flow1, flow2) { v1, v2 ->
            if (firstSkipped) {
                emit(transform(v1, v2))
            } else {
                firstSkipped = true
            }
        }.collect {
            resultFlow.value = it
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
    val resultFlow = MutableStateFlow(transform(flow1.value, flow2.value, flow3.value))
    var firstSkipped = false
    propertyHostScope.launch {
        combineTransform(flow1, flow2, flow3) { v1, v2, v3 ->
            if (firstSkipped) {
                emit(transform(v1, v2, v3))
            } else {
                firstSkipped = true
            }
        }.collect {
            resultFlow.value = it
        }
    }
    return StateDelegate(resultFlow)
}