package me.aartikov.sesame.loading.simple.internal

import me.aartikov.sesame.loading.simple.Loading.Event
import me.aartikov.sesame.loading.simple.Loading.State
import me.aartikov.sesame.loop.*

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
                    is State.Data -> when (state.refreshing) {
                        false -> next(
                            State.Data(data = state.data, refreshing = true),
                            Effect.Load(action.fresh)
                        )
                        true -> nothing()
                    }
                    is State.Error -> next(
                        State.Loading,
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
                    is State.Data -> when (state.refreshing) {
                        false -> nothing()
                        true -> next(
                            State.Data(state.data),
                            Effect.CancelLoading
                        )
                    }
                    else -> nothing()
                }
            }
        }

        is Action.DataLoaded -> {
            when (state) {
                is State.Loading -> next(State.Data(action.data))
                is State.Data -> when (state.refreshing) {
                    false -> nothing()
                    true -> next(State.Data(action.data))
                }
                else -> nothing()
            }
        }

        is Action.EmptyDataLoaded -> {
            when (state) {
                is State.Loading -> next(State.Empty)
                is State.Data -> when (state.refreshing) {
                    false -> nothing()
                    true -> next(State.Empty)
                }
                else -> nothing()
            }
        }

        is Action.LoadingError -> {
            when (state) {
                is State.Loading -> next(
                    State.Error(action.throwable),
                    Effect.EmitEvent(Event.Error(action.throwable, state))
                )
                is State.Data -> when (state.refreshing) {
                    false -> nothing()
                    true -> next(
                        State.Data(state.data),
                        Effect.EmitEvent(Event.Error(action.throwable, state))
                    )
                }
                else -> nothing()
            }
        }

        is Action.DataObserved -> {
            when (state) {
                is State.Empty -> next(State.Data(action.data))
                is State.Loading -> next(State.Data(action.data, refreshing = true))
                is State.Data -> next(State.Data(action.data, refreshing = state.refreshing))
                is State.Error -> next(State.Data(action.data))
            }
        }

        is Action.EmptyDataObserved -> {
            when (state) {
                is State.Data -> when (state.refreshing) {
                    false -> next(State.Empty)
                    true -> next(State.Loading)
                }
                else -> nothing()
            }
        }
    }
}