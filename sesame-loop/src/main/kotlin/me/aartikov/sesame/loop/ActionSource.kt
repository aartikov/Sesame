package me.aartikov.sesame.loop

/**
 * External action source.
 */
interface ActionSource<out ActionT> {

    /**
     * Run action source. Implementation can call [actionConsumer] to emit an action.
     */
    suspend fun start(actionConsumer: (ActionT) -> Unit)
}