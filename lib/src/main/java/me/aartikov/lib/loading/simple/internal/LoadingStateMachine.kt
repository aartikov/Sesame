package me.aartikov.lib.loading.simple.internal

import me.aartikov.lib.state_machine.*
import me.aartikov.lib.loading.simple.Loading.Event
import me.aartikov.lib.loading.simple.Loading.State

internal sealed class Action<out T> {
    data class Load(val fresh: Boolean) : Action<Nothing>()
    object Refresh : Action<Nothing>()

    object LoadingStarted : Action<Nothing>()
    data class FreshData<T>(val data: T) : Action<T>()
    object FreshEmptyData : Action<Nothing>()
    data class LoadingError(val throwable: Throwable) : Action<Nothing>()

    data class CachedData<T>(val data: T) : Action<T>()
    object CachedEmptyData : Action<Nothing>()
    data class CacheError(val throwable: Throwable) : Action<Nothing>()
}

internal sealed class Effect {
    data class Load(val fresh: Boolean) : Effect()
    object Refresh : Effect()
    data class EmitEvent(val event: Event) : Effect()
}

internal typealias LoadingStateMachine<T> = StateMachine<State<T>, Action<T>, Effect>

internal class LoadingReducer<T> : Reducer<State<T>, Action<T>, Effect> {

    override fun reduce(state: State<T>, action: Action<T>): Next<State<T>, Effect> = when (action) {

        is Action.Load -> {
            when (state) {
                is State.Empty -> effects(Effect.Load(action.fresh))
                else -> nothing()
            }
        }

        is Action.Refresh -> {
            when (state) {
                is State.Empty -> effects(Effect.Refresh)
                is State.EmptyError -> effects(Effect.Refresh)
                is State.Data -> effects(Effect.Refresh)
                else -> nothing()
            }
        }

        is Action.LoadingStarted -> {
            when (state) {
                is State.Empty -> next(State.EmptyLoading)
                is State.EmptyError -> next(State.EmptyLoading)
                is State.Data -> next(State.Refresh(state.data))
                else -> nothing()
            }
        }

        is Action.FreshData -> {
            when (state) {
                is State.EmptyLoading -> next(State.Data(action.data))
                is State.Refresh -> next(State.Data(action.data))
                else -> nothing()
            }
        }

        is Action.FreshEmptyData -> {
            when (state) {
                is State.EmptyLoading -> next(State.Empty)
                is State.Refresh -> next(State.Empty)
                else -> nothing()
            }
        }

        is Action.LoadingError -> {
            when (state) {
                is State.EmptyLoading -> next(
                    State.EmptyError(action.throwable),
                    Effect.EmitEvent(Event.Error(action.throwable, hasData = false))
                )
                is State.Refresh -> next(
                    State.Data(state.data),
                    Effect.EmitEvent(Event.Error(action.throwable, hasData = true))
                )
                else -> nothing()
            }
        }

        is Action.CachedData -> {
            when (state) {
                is State.Empty -> next(State.Data(action.data))
                is State.EmptyLoading -> next(State.Refresh(action.data))
                is State.EmptyError -> next(State.Data(action.data))
                is State.Data -> next(State.Data(action.data))
                is State.Refresh -> next(State.Refresh(action.data))
            }
        }

        is Action.CachedEmptyData -> {
            when (state) {
                is State.Data -> next(State.Empty)
                is State.Refresh -> next(State.EmptyLoading)
                else -> nothing()
            }
        }

        is Action.CacheError -> { // TODO: show it as EmptyError in some cases?
            val hasData = state is State.Data || state is State.Refresh
            effects(Effect.EmitEvent(Event.Error(action.throwable, hasData)))
        }
    }
}