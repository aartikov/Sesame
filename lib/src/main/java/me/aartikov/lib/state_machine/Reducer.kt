package me.aartikov.lib.state_machine

/**
 * Executes action processing loop.
 */
interface Reducer<StateT, ActionT, EffectT> {

    /**
     * Receives current state and an action, generates a [Next]-object.
     * Can't execute side effects directly. Must be a pure function.
     */
    fun reduce(state: StateT, action: ActionT): Next<StateT, EffectT>
}