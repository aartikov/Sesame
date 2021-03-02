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

/**
 * Handles only side effects of specified subtype.
 */
abstract class SubtypeEffectHandler<EffectT, SubtypeEffectT : EffectT, out ActionT>(
    private val subType: Class<SubtypeEffectT>
) : EffectHandler<EffectT, ActionT> {

    final override suspend fun handleEffect(effect: EffectT, actionConsumer: (ActionT) -> Unit) {
        if (subType.isInstance(effect)) {
            @Suppress("UNCHECKED_CAST")
            handleSubtypeEffect(effect as SubtypeEffectT, actionConsumer)
        }
    }

    abstract suspend fun handleSubtypeEffect(effect: SubtypeEffectT, actionConsumer: (ActionT) -> Unit)
}