package me.aartikov.lib.loading.paged.internal

import me.aartikov.lib.loading.paged.PagedLoading.Event
import me.aartikov.lib.loading.paged.PagingInfo
import me.aartikov.lib.loop.*

internal sealed class State<out T> {
    object Empty : State<Nothing>()
    object Loading : State<Nothing>()
    data class Error(val throwable: Throwable) : State<Nothing>()
    data class Data<T>(val pageCount: Int, val data: List<T>) : State<T>()
    data class Refresh<T>(val pageCount: Int, val data: List<T>) : State<T>()
    data class LoadingMore<T>(val pageCount: Int, val data: List<T>) : State<T>()
    data class FullData<T>(val pageCount: Int, val data: List<T>) : State<T>()
}

internal sealed class Action<out T> {
    data class Load(val fresh: Boolean) : Action<Nothing>()
    object Refresh : Action<Nothing>()
    object LoadMore : Action<Nothing>()
    data class Restart(val fresh: Boolean) : Action<Nothing>()

    data class NewPage<T>(val data: List<T>) : Action<T>()
    object EmptyPage : Action<Nothing>()
    data class Error(val throwable: Throwable) : Action<Nothing>()
}

internal sealed class Effect<out T> {
    data class LoadFirstPage(val fresh: Boolean) : Effect<Nothing>()
    data class LoadNextPage<T>(val pagingInfo: PagingInfo<T>) : Effect<T>()
    data class EmitEvent(val event: Event) : Effect<Nothing>()
}

internal typealias PagedLoadingLoop<T> = Loop<State<T>, Action<T>, Effect<T>>

internal class PagedLoadingReducer<T> : Reducer<State<T>, Action<T>, Effect<T>> {

    override fun reduce(state: State<T>, action: Action<T>): Next<State<T>, Effect<T>> = when (action) {
        is Action.Load -> {
            when (state) {
                is State.Empty -> next(
                    State.Loading,
                    Effect.LoadFirstPage(action.fresh)
                )
                else -> nothing()
            }
        }

        is Action.Refresh -> {
            when (state) {
                is State.Empty -> next(
                    State.Loading,
                    Effect.LoadFirstPage(fresh = true)
                )
                is State.Error -> next(
                    State.Loading,
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

        is Action.Restart -> {
            next(
                State.Loading,
                Effect.LoadFirstPage(action.fresh)
            )
        }

        is Action.NewPage -> {
            when (state) {
                is State.Loading -> next(State.Data(1, action.data))
                is State.Refresh -> next(State.Data(1, action.data))
                is State.LoadingMore -> next(State.Data(state.pageCount + 1, state.data + action.data))
                else -> nothing()
            }
        }

        is Action.EmptyPage -> {
            when (state) {
                is State.Loading -> next(State.Empty)
                is State.Refresh -> next(State.Empty)
                is State.LoadingMore -> next(State.FullData(state.pageCount, state.data))
                else -> nothing()
            }
        }

        is Action.Error -> {
            when (state) {
                is State.Loading -> next(
                    State.Error(action.throwable),
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