package me.aartikov.lib.loading.simple.internal

import kotlinx.coroutines.channels.SendChannel
import me.aartikov.lib.state_machine.EffectHandler
import me.aartikov.lib.loading.simple.Loading

internal class EventEffectHandler<T>(private val eventChannel: SendChannel<Loading.Event>) :
    EffectHandler<Effect, Action<T>> {

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.EmitEvent -> eventChannel.offer(effect.event)
        }
    }
}