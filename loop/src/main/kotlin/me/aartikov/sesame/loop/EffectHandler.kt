package me.aartikov.sesame.loop

/**
 * Executes side effects. It also can generate its own actions.
 */
interface EffectHandler<in EffectT, out ActionT> {
    /**
     * [actionConsumer] can be called to emit an action
     */
    suspend fun handleEffect(effect: EffectT, actionConsumer: (ActionT) -> Unit)
}