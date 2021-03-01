package me.aartikov.sesame.loading.simple.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.Loading.Event
import me.aartikov.sesame.loading.simple.Loading.State
import me.aartikov.sesame.loop.ActionSource
import me.aartikov.sesame.loop.EffectHandler

internal class LoadingImpl<T : Any>(
    loadingEffectHandler: EffectHandler<Effect, Action<T>>,
    loadingActionSource: ActionSource<Action<T>>?,
    initialState: State<T>
) : Loading<T> {

    private val mutableStateFlow = MutableStateFlow(initialState)

    private val mutableEventFlow = MutableSharedFlow<Event>(
        extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val loop: LoadingLoop<T> = LoadingLoop(
        initialState = initialState.toInternalState(),
        reducer = LoadingReducer(),
        effectHandlers = listOf(
            loadingEffectHandler,
            EventEffectHandler { event -> mutableEventFlow.tryEmit(event) }
        ),
        actionSources = listOfNotNull(
            loadingActionSource
        )
    )

    override val stateFlow: StateFlow<State<T>>
        get() = mutableStateFlow

    override val eventFlow: Flow<Event>
        get() = mutableEventFlow

    override fun attach(scope: CoroutineScope): Job = scope.launch {
        coroutineScope {
            launch {
                loop.stateFlow.collect {
                    mutableStateFlow.value = it.toPublicState()
                }
            }

            launch {
                loop.start()
            }
        }
    }

    override fun load(fresh: Boolean, reset: Boolean) {
        loop.dispatch(Action.Load(fresh, reset))
    }

    override fun cancel(reset: Boolean) {
        loop.dispatch(Action.Cancel(reset))
    }
}