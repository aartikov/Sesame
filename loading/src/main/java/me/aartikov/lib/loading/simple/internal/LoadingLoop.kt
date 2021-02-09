package me.aartikov.lib.loading.simple.internal

import me.aartikov.lib.loading.simple.Loading.Event
import me.aartikov.lib.loop.*

internal sealed class State<out T> {
    object Empty : State<Nothing>()
    object Loading : State<Nothing>()
    data class Error(val throwable: Throwable) : State<Nothing>()
    data class Data<T>(val data: T) : State<T>()
    data class Refresh<T>(val data: T) : State<T>()
}

internal sealed class Action<out T> {
    data class Load(val fresh: Boolean) : Action<Nothing>()
    object Refresh : Action<Nothing>()
    data class Restart(val fresh: Boolean) : Action<Nothing>()

    data class DataLoaded<T>(val data: T) : Action<T>()
    object EmptyDataLoaded : Action<Nothing>()
    data class LoadingError(val throwable: Throwable) : Action<Nothing>()

    data class DataObserved<T>(val data: T) : Action<T>()
    object EmptyDataObserved : Action<Nothing>()
}

internal sealed class Effect {
    data class Load(val fresh: Boolean) : Effect()
    data class EmitEvent(val event: Event) : Effect()
}

internal typealias LoadingLoop<T> = Loop<State<T>, Action<T>, Effect>

internal class LoadingReducer<T> : Reducer<State<T>, Action<T>, Effect> {

    override fun reduce(state: State<T>, action: Action<T>): Next<State<T>, Effect> = when (action) {

        is Action.Load -> {
            when (state) {
                is State.Empty -> next(
                    State.Loading,
                    Effect.Load(action.fresh)
                )
                else -> nothing()
            }
        }

        is Action.Refresh -> {
            when (state) {
                is State.Empty -> next(
                    State.Loading,
                    Effect.Load(fresh = true)
                )
                is State.Error -> next(
                    State.Loading,
                    Effect.Load(fresh = true)
                )
                is State.Data -> next(
                    State.Refresh(data = state.data),
                    Effect.Load(fresh = true)
                )
                else -> nothing()
            }
        }

        is Action.Restart -> {
            next(
                State.Loading,
                Effect.Load(action.fresh)
            )
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
                    Effect.EmitEvent(Event.Error(action.throwable, hasData = false))
                )
                is State.Refresh -> next(
                    State.Data(state.data),
                    Effect.EmitEvent(Event.Error(action.throwable, hasData = true))
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