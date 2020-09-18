package me.aartikov.lib.loading.paged.internal

import me.aartikov.lib.loading.paged.PagedLoading.Event
import me.aartikov.lib.loading.paged.PagedLoading.State
import me.aartikov.lib.loading.paged.PagingInfo
import me.aartikov.lib.state_machine.*

internal sealed class Action<out T> {
    data class Load(val fresh: Boolean) : Action<Nothing>()
    object Refresh : Action<Nothing>()
    object LoadMore : Action<Nothing>()

    data class NewPage<T>(val data: List<T>) : Action<T>()
    object EmptyPage : Action<Nothing>()
    data class Error(val throwable: Throwable) : Action<Nothing>()
}

internal sealed class Effect<out T> {
    data class LoadFirstPage(val fresh: Boolean) : Effect<Nothing>()
    data class LoadNextPage<T>(val pagingInfo: PagingInfo<T>) : Effect<T>()
    data class EmitEvent(val event: Event) : Effect<Nothing>()
}

internal typealias PagedLoadingStateMachine<T> = StateMachine<State<T>, Action<T>, Effect<T>>

internal class PagedLoadingReducer<T> : Reducer<State<T>, Action<T>, Effect<T>> {

    override fun reduce(state: State<T>, action: Action<T>): Next<State<T>, Effect<T>> = when (action) {
        is Action.Load -> {
            when (state) {
                is State.Empty -> next(
                    State.EmptyLoading,
                    Effect.LoadFirstPage(action.fresh)
                )
                else -> nothing()
            }
        }

        is Action.Refresh -> {
            when (state) {
                is State.Empty -> next(
                    State.EmptyLoading,
                    Effect.LoadFirstPage(fresh = true)
                )
                is State.EmptyError -> next(
                    State.EmptyLoading,
                    Effect.LoadFirstPage(fresh = true)
                )
                is State.Data -> next(
                    State.Refresh(state.pageCount, state.data),
                    Effect.LoadFirstPage(fresh = true)
                )
                is State.LoadingMore -> next(
                    State.Refresh(state.pageCount, state.data),
                    Effect.LoadFirstPage(fresh = true)
                )
                is State.FullData -> next(
                    State.Refresh(state.pageCount, state.data),
                    Effect.LoadFirstPage(fresh = true)
                )
                else -> nothing()
            }
        }

        is Action.LoadMore -> {
            when (state) {
                is State.Data -> next(
                    State.LoadingMore(state.pageCount, state.data),
                    Effect.LoadNextPage(PagingInfo(state.pageCount, state.data))
                )
                else -> nothing()
            }
        }

        is Action.NewPage -> {
            when (state) {
                is State.EmptyLoading -> next(State.Data(1, action.data))
                is State.Refresh -> next(State.Data(1, action.data))
                is State.LoadingMore -> next(State.Data(state.pageCount + 1, state.data + action.data))
                else -> nothing()
            }
        }

        is Action.EmptyPage -> {
            when (state) {
                is State.EmptyLoading -> next(State.Empty)
                is State.Refresh -> next(State.Empty)
                is State.LoadingMore -> next(State.FullData(state.pageCount, state.data))
                else -> nothing()
            }
        }

        is Action.Error -> {
            when (state) {
                is State.EmptyLoading -> next(
                    State.EmptyError(action.throwable),
                    Effect.EmitEvent(Event.Error(action.throwable, hasData = false))
                )
                is State.Refresh -> next(
                    State.Data(state.pageCount, state.data),
                    Effect.EmitEvent(Event.Error(action.throwable, hasData = true))
                )
                is State.LoadingMore -> next(
                    State.Data(state.pageCount, state.data),
                    Effect.EmitEvent(Event.Error(action.throwable, hasData = true))
                )
                else -> nothing()
            }
        }
    }
}