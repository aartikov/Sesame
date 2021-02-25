package me.aartikov.sesame.property

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty0

/**
 * Creates a read-only observable property that is automatically kept in sync with the other observable [property].
 */
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

/**
 * Creates a read-only observable property that is automatically kept in sync with the other observable properties.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, R> PropertyHost.computed(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    transform: (T1, T2) -> R
): StateDelegate<R> {
    return computedImpl(property1, property2) { args: List<*> ->
        transform(
            args[0] as T1,
            args[1] as T2
        )
    }
}

/**
 * Creates a read-only observable property that is automatically kept in sync with the other observable properties.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, R> PropertyHost.computed(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    transform: (T1, T2, T3) -> R
): StateDelegate<R> {
    return computedImpl(property1, property2, property3) { args: List<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3
        )
    }
}

/**
 * Creates a read-only observable property that is automatically kept in sync with the other observable properties.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, R> PropertyHost.computed(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    transform: (T1, T2, T3, T4) -> R
): StateDelegate<R> {
    return computedImpl(property1, property2, property3, property4) { args: List<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4
        )
    }
}

/**
 * Creates a read-only observable property that is automatically kept in sync with the other observable properties.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, R> PropertyHost.computed(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    property5: KProperty0<T5>,
    transform: (T1, T2, T3, T4, T5) -> R
): StateDelegate<R> {
    return computedImpl(property1, property2, property3, property4, property5) { args: List<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5
        )
    }
}

private inline fun <T, R> PropertyHost.computedImpl(
    vararg properties: KProperty0<T>,
    crossinline transform: (List<T>) -> R
): StateDelegate<R> {

    val flows = properties.map { it.flow }
    val initialValues = flows.map { it.value }
    val elementsFlow = MutableStateFlow(initialValues)
    val resultFlow = MutableStateFlow(transform(initialValues))

    flows.forEachIndexed { index, flow ->
        propertyHostScope.launch {
            flow
                .drop(1)
                .collect {
                    elementsFlow.value = elementsFlow.value.toMutableList().apply { this[index] = it }
                }
        }
    }

    propertyHostScope.launch {
        elementsFlow
            .drop(1)
            .collect {
                resultFlow.value = transform(it)
            }
    }

    return StateDelegate(resultFlow)
}