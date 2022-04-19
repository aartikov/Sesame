package me.aartikov.sesame.property

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty0

/**
 * Runs a [block] whenever the given observable [property] is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
fun <T> PropertyHost.autorun(
    property: KProperty0<T>,
    debounceTimeoutMillis: Long? = null,
    block: (T) -> Unit
) {
    val flow = property.flow
    propertyHostScope.launch {
        flow
            .let {
                if (debounceTimeoutMillis != null) {
                    it.debounce(debounceTimeoutMillis)
                } else {
                    it
                }
            }
            .collect {
                block.invoke(it)
            }
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2, T3) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        property3,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2, T3, T4) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        property3,
        property4,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
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
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    property5: KProperty0<T5>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2, T3, T4, T5) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        property3,
        property4,
        property5,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    property5: KProperty0<T5>,
    property6: KProperty0<T6>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2, T3, T4, T5, T6) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        property3,
        property4,
        property5,
        property6,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6, T7> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    property5: KProperty0<T5>,
    property6: KProperty0<T6>,
    property7: KProperty0<T7>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2, T3, T4, T5, T6, T7) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        property3,
        property4,
        property5,
        property6,
        property7,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6, T7, T8> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    property5: KProperty0<T5>,
    property6: KProperty0<T6>,
    property7: KProperty0<T7>,
    property8: KProperty0<T8>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2, T3, T4, T5, T6, T7, T8) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        property3,
        property4,
        property5,
        property6,
        property7,
        property8,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
            args[7] as T8
        )
    }
}

/**
 * Runs a [block] whenever any of the given observable properties is changed.
 * @param debounceTimeoutMillis optional debounce setting.
 */
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> PropertyHost.autorun(
    property1: KProperty0<T1>,
    property2: KProperty0<T2>,
    property3: KProperty0<T3>,
    property4: KProperty0<T4>,
    property5: KProperty0<T5>,
    property6: KProperty0<T6>,
    property7: KProperty0<T7>,
    property8: KProperty0<T8>,
    property9: KProperty0<T9>,
    debounceTimeoutMillis: Long? = null,
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> Unit
) {
    autorunImpl(
        property1,
        property2,
        property3,
        property4,
        property5,
        property6,
        property7,
        property8,
        property9,
        debounceTimeoutMillis = debounceTimeoutMillis
    ) { args: List<*> ->
        block(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
            args[7] as T8,
            args[8] as T9
        )
    }
}

private inline fun <T> PropertyHost.autorunImpl(
    vararg properties: KProperty0<T>,
    debounceTimeoutMillis: Long?,
    crossinline block: (List<T>) -> Unit
) {
    val flows = properties.map { it.flow }
    val initialValues = flows.map { it.value }
    val elementsFlow = MutableStateFlow(initialValues)

    flows.forEachIndexed { index, flow ->
        propertyHostScope.launch {
            flow
                .collect {
                    elementsFlow.value =
                        elementsFlow.value.toMutableList().apply { this[index] = it }
                }
        }
    }

    propertyHostScope.launch {
        elementsFlow
            .let {
                if (debounceTimeoutMillis != null) {
                    it.debounce(debounceTimeoutMillis)
                } else {
                    it
                }
            }
            .collect {
                block.invoke(it)
            }
    }
}