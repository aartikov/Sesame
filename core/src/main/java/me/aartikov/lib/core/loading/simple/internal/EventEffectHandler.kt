package me.aartikov.lib.core.loading.simple.internal

import me.aartikov.lib.core.loading.simple.Loading
import me.aartikov.lib.core.loop.EffectHandler

internal class EventEffectHandler<T>(private val emitEvent: (Loading.Event) -> Unit) :
    EffectHandler<Effect, Action<T>> {

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.EmitEvent -> emitEvent(effect.event)
        }
    }
}