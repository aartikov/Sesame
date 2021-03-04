package me.aartikov.sesame.loop

/**
 * Logs what happens inside [Loop].
 */
interface LoopLogger<StateT, ActionT, EffectT> {

    /**
     * Logs when loop starts.
     * @param state initial state.
     */
    fun logOnStarted(state: StateT)

    /**
     * Logs when new action arrived before a reducer has been called.
     * @param state current state.
     * @param action arrived action.
     */
    fun logBeforeReduce(state: StateT, action: ActionT)

    /**
     * Logs after a reducer has been called.
     * @param previousState state that was passed to a reducer.
     * @param action arrived action.
     * @param next contains new state and side effects (see: [Next]).
     */
    fun logAfterReduce(previousState: StateT, action: ActionT, next: Next<StateT, EffectT>)
}