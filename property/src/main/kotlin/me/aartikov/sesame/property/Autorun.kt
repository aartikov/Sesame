package me.aartikov.sesame.property

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty0

/**
 * Runs a [block] whenever the given observable [property] is changed.
 */
fun <T> PropertyHost.autorun(
    property: KProperty0<T>,
    block: (T) -> Unit
) {
    val flow = property.flow

    propertyHostScope.launch {
        flow
            .collect {
                block.invoke(it)
            }
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 */
@Suppress("UNCHECKED_CAST")
fun<T1, T2> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    block: (T1, T2) -> Unit
) {
    autorunImpl(property1, property2) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 */
@Suppress("UNCHECKED_CAST")
fun<T1, T2, T3> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    block: (T1, T2, T3) -> Unit
) {
    autorunImpl(property1, property2, property3) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 */
@Suppress("UNCHECKED_CAST")
fun<T1, T2, T3, T4> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    block: (T1, T2, T3, T4) -> Unit
) {
    autorunImpl(property1, property2, property3, property4) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 */
@Suppress("UNCHECKED_CAST")
fun<T1, T2, T3, T4, T5> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    property5: KProperty0<T5>,
    block: (T1, T2, T3, T4, T5) -> Unit
) {
    autorunImpl(property1, property2, property3, property4, property5) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5
        )
    }
}

private inline fun<T> PropertyHost.autorunImpl(
    vararg properties: KProperty0<T>,
    crossinline block: (List<T>) -> Unit
) {
    val flows = properties.map { it.flow }
    val initialValues = flows.map { it.value }
    val elementsFlow = MutableStateFlow(initialValues)

    flows.forEachIndexed { index, flow ->
        propertyHostScope.launch {
            flow
                .collect {
                    elementsFlow.value = elementsFlow.value.toMutableList().apply { this[index] = it }
                }
        }
    }

    propertyHostScope.launch {
        elementsFlow.collect {
            block.invoke(it)
        }
    }
}