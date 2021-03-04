package me.aartikov.sesame.loop

/**
 * Represents an output of [Reducer]. Contains new state and side effects. Null state means no changes.
 */
data class Next<StateT, EffectT>(val state: StateT?, val effects: List<EffectT>)


/**
 * Helper method to create [Next]
 */
fun <StateT, EffectT> next(state: StateT, vararg effects: EffectT?): Next<StateT, EffectT> {
    return Next(state, effects.toList().filterNotNull())
}

fun <StateT, EffectT> next(state: StateT): Next<StateT, EffectT> {
    return Next(state, emptyList())
}

fun <StateT, EffectT> effects(vararg effects: EffectT?): Next<StateT, EffectT> {
    return Next(null, effects.toList().filterNotNull())
}

fun <StateT, EffectT> nothing(): Next<StateT, EffectT> {
    return Next(null, emptyList())
}