package me.aartikov.sesame.loading.paged.internal

import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesame.loop.SubtypeEffectHandler

internal class EventEffectHandler<T>(private val emitEvent: (PagedLoading.Event) -> Unit) :
    SubtypeEffectHandler<Effect<T>, Effect.EmitEvent, Action<T>>(Effect.EmitEvent::class.java) {

    override suspend fun handleSubtypeEffect(effect: Effect.EmitEvent, actionConsumer: (Action<T>) -> Unit) {
        emitEvent(effect.event)
    }
}