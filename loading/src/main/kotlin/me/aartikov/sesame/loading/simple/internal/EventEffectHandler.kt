package me.aartikov.sesame.loading.simple.internal

import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loop.EffectHandler

internal class EventEffectHandler<T>(private val emitEvent: (Loading.Event) -> Unit) :
    EffectHandler<Effect, Action<T>> {

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.EmitEvent -> emitEvent(effect.event)
        }
    }
}