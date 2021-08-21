package me.aartikov.sesame.loading.simple.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.Loading.Event
import me.aartikov.sesame.loading.simple.Loading.State
import me.aartikov.sesame.loop.ActionSource
import me.aartikov.sesame.loop.EffectHandler

internal class LoadingImpl<T : Any>(
    scope: CoroutineScope,
    loadingEffectHandler: EffectHandler<Effect<T>, Action<T>>,
    loadingActionSource: ActionSource<Action<T>>?,
    initialState: State<T>
) : Loading<T> {

    private val mutableEventFlow = MutableSharedFlow<Event<T>>(
        extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val loop: LoadingLoop<T> = LoadingLoop(
        initialState = initialState,
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
        get() = loop.stateFlow

    override val eventFlow: Flow<Event<T>>
        get() = mutableEventFlow

    init {
        scope.launch {
            loop.start()
        }
    }

    override fun load(fresh: Boolean, reset: Boolean) {
        loop.dispatch(Action.Load(fresh, reset))
    }

    override fun cancel(reset: Boolean) {
        loop.dispatch(Action.Cancel(reset))
    }
}