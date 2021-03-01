package me.aartikov.sesame.loop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * TEA-like state manager. Stores current state. Receives actions from [dispatch] method or from external [actionSources].
 * Sends actions to [Reducer]. [Reducer] generates [Next]-object (new state + side effects). Side effects are handled by [effectHandlers].
 *
 * [StateT] - type of state that is stored in a state manager. State must be immutable
 * [ActionT] - type of action (some external command)
 * [EffectT] - type of side effects (such as starting a network request or saving to database)
 */
open class Loop<StateT, ActionT, EffectT>(
    initialState: StateT,
    private val reducer: Reducer<StateT, ActionT, EffectT>,
    private val effectHandlers: List<EffectHandler<EffectT, ActionT>> = emptyList(),
    private val actionSources: List<ActionSource<ActionT>> = emptyList(),
    private val logger: LoopLogger<StateT, ActionT, EffectT>? = null
) {

    /**
     * Current state
     */
    val state: StateT get() = stateFlow.value

    /**
     * Flow of state changes
     */
    val stateFlow: StateFlow<StateT> get() = mutableStateFlow

    /**
     * Returns true if [start] was called and is not canceled yet.
     */
    var started: Boolean = false
        private set

    private val mutableStateFlow = MutableStateFlow(initialState)
    private val actionChannel = Channel<ActionT>(Channel.UNLIMITED)

    /**
     * Starts loop
     */
    suspend fun start() {
        if (started) {
            throw IllegalStateException("Loop is already started")
        }
        started = true
        logger?.logOnStarted(state)

        coroutineScope {
            try {
                startActionSources(this)
                for (action in actionChannel) {
                    logger?.logBeforeReduce(state, action)
                    val next = reducer.reduce(state, action)
                    logger?.logAfterReduce(state, action, next)
                    changeState(next.state)
                    handleEffects(this, next.effects)
                }
            } finally {
                started = false
            }
        }
    }

    /**
     * Sends action for processing
     */
    fun dispatch(action: ActionT) {
        actionChannel.offer(action)
    }

    private suspend fun startActionSources(scope: CoroutineScope) {
        actionSources.forEach { source ->
            scope.launch {
                source.start { action ->
                    dispatch(action)
                }
            }
        }
    }

    private fun changeState(newState: StateT?) {
        if (newState != null) {
            mutableStateFlow.value = newState
        }
    }

    private suspend fun handleEffects(scope: CoroutineScope, effects: List<EffectT>) {
        effects.forEach { effect ->
            effectHandlers.forEach { handler ->
                scope.launch {
                    handler.handleEffect(effect) { action ->
                        dispatch(action)
                    }
                }
            }
        }
    }
}

/**
 * A helper method to start [Loop] in a [scope].
 */
fun <StateT, ActionT, EffectT> Loop<StateT, ActionT, EffectT>.startIn(scope: CoroutineScope) {
    scope.launch {
        start()
    }
}