package me.aartikov.lib.loading.simple.internal

import me.aartikov.lib.loop.EffectHandler
import me.aartikov.lib.loading.simple.Loading

internal class EventEffectHandler<T>(private val emitEvent: (Loading.Event) -> Unit) :
    EffectHandler<Effect, Action<T>> {

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.EmitEvent -> emitEvent(effect.event)
        }
    }
}