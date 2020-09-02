package me.aartikov.lib.loading.simple.internal

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import me.aartikov.lib.state_machine.EffectHandler
import me.aartikov.lib.loading.simple.Loading
import me.aartikov.lib.loading.simple.Loading.Event
import me.aartikov.lib.loading.simple.Loading.State

internal class LoadingImpl<T : Any>(
    loadingEffectHandler: EffectHandler<Effect, Action<T>>,
    private val initialState: State<T> = State.Empty
) : Loading<T> {

    private val eventChannel = BroadcastChannel<Event>(capacity = 100)

    private val stateMachine: LoadingStateMachine<T> = LoadingStateMachine(
        initialState = initialState,
        reducer = LoadingReducer(),
        actionSources = emptyList(),
        effectHandlers = listOf(
            loadingEffectHandler,
            EventEffectHandler(eventChannel)
        )
    )

    override val stateFlow: StateFlow<State<T>>
        get() = stateMachine.stateFlow

    override val eventFlow: Flow<Event>
        get() = eventChannel.asFlow()

    override suspend fun start(fresh: Boolean) {
        stateMachine.dispatch(Action.Load(fresh))
        stateMachine.start()
    }

    override fun refresh() {
        stateMachine.dispatch(Action.Refresh)
    }
}