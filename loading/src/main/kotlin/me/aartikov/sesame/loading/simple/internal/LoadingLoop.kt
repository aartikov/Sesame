package me.aartikov.sesame.loading.simple.internal

import me.aartikov.sesame.loading.simple.Loading.Event
import me.aartikov.sesame.loop.*

internal sealed class State<out T> {
    object Empty : State<Nothing>()
    object Loading : State<Nothing>()
    data class Error(val throwable: Throwable) : State<Nothing>()
    data class Data<T>(val data: T) : State<T>()
    data class Refresh<T>(val data: T) : State<T>()
}

internal sealed class Action<out T> {
    data class Load(val fresh: Boolean, val reset: Boolean) : Action<Nothing>()
    data class Cancel(val reset: Boolean) : Action<Nothing>()

    data class DataLoaded<T>(val data: T) : Action<T>()
    object EmptyDataLoaded : Action<Nothing>()
    data class LoadingError(val throwable: Throwable) : Action<Nothing>()

    data class DataObserved<T>(val data: T) : Action<T>()
    object EmptyDataObserved : Action<Nothing>()
}

internal sealed class Effect<out T> {
    data class Load(val fresh: Boolean) : Effect<Nothing>()
    object CancelLoading : Effect<Nothing>()
    data class EmitEvent<T>(val event: Event<T>) : Effect<T>()
}

internal typealias LoadingLoop<T> = Loop<State<T>, Action<T>, Effect<T>>

internal class LoadingReducer<T> : Reducer<State<T>, Action<T>, Effect<T>> {

    override fun reduce(state: State<T>, action: Action<T>): Next<State<T>, Effect<T>> = when (action) {

        is Action.Load -> {
            if (action.reset) {
                next(
                    State.Loading,
                    Effect.Load(action.fresh)
                )
            } else {
                when (state) {
                    is State.Empty -> next(
                        State.Loading,
                        Effect.Load(action.fresh)
                    )
                    is State.Error -> next(
                        State.Loading,
                        Effect.Load(action.fresh)
                    )
                    is State.Data -> next(
                        State.Refresh(data = state.data),
                        Effect.Load(action.fresh)
                    )
                    else -> nothing()
                }
            }
        }

        is Action.Cancel -> {
            if (action.reset) {
                next(
                    State.Empty,
                    Effect.CancelLoading
                )
            } else {
                when (state) {
                    is State.Loading -> next(
                        State.Empty,
                        Effect.CancelLoading
                    )
                    is State.Refresh -> next(
                        State.Data(state.data),
                        Effect.CancelLoading
                    )
                    else -> nothing()
                }
            }
        }

        is Action.DataLoaded -> {
            when (state) {
                is State.Loading -> next(State.Data(action.data))
                is State.Refresh -> next(State.Data(action.data))
                else -> nothing()
            }
        }

        is Action.EmptyDataLoaded -> {
            when (state) {
                is State.Loading -> next(State.Empty)
                is State.Refresh -> next(State.Empty)
                else -> nothing()
            }
        }

        is Action.LoadingError -> {
            when (state) {
                is State.Loading -> next(
                    State.Error(action.throwable),
                    Effect.EmitEvent(Event.Error(action.throwable, state.toPublicState()))
                )
                is State.Refresh -> next(
                    State.Data(state.data),
                    Effect.EmitEvent(Event.Error(action.throwable, state.toPublicState()))
                )
                else -> nothing()
            }
        }

        is Action.DataObserved -> {
            when (state) {
                is State.Empty -> next(State.Data(action.data))
                is State.Loading -> next(State.Refresh(action.data))
                is State.Error -> next(State.Data(action.data))
                is State.Refresh -> next(State.Refresh(action.data))
                is State.Data -> next(State.Data(action.data))
            }
        }

        is Action.EmptyDataObserved -> {
            when (state) {
                is State.Refresh -> next(State.Loading)
                is State.Data -> next(State.Empty)
                else -> nothing()
            }
        }
    }
}