package me.aartikov.lib.loading.simple.internal

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.aartikov.lib.loading.simple.Loading
import me.aartikov.lib.loading.simple.Loading.Event
import me.aartikov.lib.loading.simple.Loading.State
import me.aartikov.lib.state_machine.ActionSource
import me.aartikov.lib.state_machine.EffectHandler

internal class LoadingImpl<T : Any>(
    loadingEffectHandler: EffectHandler<Effect, Action<T>>,
    loadingActionSource: ActionSource<Action<T>>?,
    initialState: State<T>
) : Loading<T> {

    private val mutableStateFlow = MutableStateFlow(initialState)
    private val eventChannel = BroadcastChannel<Event>(capacity = 100)

    private val stateMachine: LoadingStateMachine<T> = LoadingStateMachine(
        initialState = initialState.toInternalState(),
        reducer = LoadingReducer(),
        actionSources = listOfNotNull(
            loadingActionSource
        ),
        effectHandlers = listOf(
            loadingEffectHandler,
            EventEffectHandler(eventChannel)
        )
    )

    override val stateFlow: StateFlow<State<T>>
        get() = mutableStateFlow

    override val eventFlow: Flow<Event>
        get() = eventChannel.asFlow()

    override suspend fun start(fresh: Boolean) = coroutineScope {
        launch {
            stateMachine.stateFlow.collect {
                mutableStateFlow.value = it.toPublicState()
            }
        }
        stateMachine.dispatch(Action.Load(fresh))
        stateMachine.start()
    }

    override fun refresh() {
        stateMachine.dispatch(Action.Refresh)
    }
}