package me.aartikov.sesame.loading.simple.internal

import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loop.EffectHandler

internal class EventEffectHandler<T : Any>(private val emitEvent: (Loading.Event<T>) -> Unit) :
    EffectHandler<Effect<T>, Action<T>> {

    override suspend fun handleEffect(effect: Effect<T>, actionConsumer: (Action<T>) -> Unit) {
        if (effect is Effect.EmitEvent<T>) {
            emitEvent(effect.event)
        }
    }
}