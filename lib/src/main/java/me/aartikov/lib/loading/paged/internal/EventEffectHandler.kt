package me.aartikov.lib.loading.paged.internal

import kotlinx.coroutines.channels.SendChannel
import me.aartikov.lib.loading.paged.PagedLoading
import me.aartikov.lib.state_machine.EffectHandler

internal class EventEffectHandler<T>(private val eventChannel: SendChannel<PagedLoading.Event>) :
    EffectHandler<Effect<T>, Action<T>> {

    override suspend fun handleEffect(effect: Effect<T>, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.EmitEvent -> eventChannel.offer(effect.event)
        }
    }
}