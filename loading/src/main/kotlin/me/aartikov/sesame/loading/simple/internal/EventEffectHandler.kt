package me.aartikov.sesame.loading.simple.internal

import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loop.SubtypeEffectHandler

internal class EventEffectHandler<T>(private val emitEvent: (Loading.Event) -> Unit) :
    SubtypeEffectHandler<Effect, Effect.EmitEvent, Action<T>>(Effect.EmitEvent::class.java) {

    override suspend fun handleSubtypeEffect(effect: Effect.EmitEvent, actionConsumer: (Action<T>) -> Unit) {
        emitEvent(effect.event)
    }
}