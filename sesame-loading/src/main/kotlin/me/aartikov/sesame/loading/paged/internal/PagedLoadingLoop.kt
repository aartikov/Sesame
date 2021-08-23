package me.aartikov.sesame.loading.paged.internal

import me.aartikov.sesame.loading.paged.Page
import me.aartikov.sesame.loading.paged.PagedLoading.*
import me.aartikov.sesame.loading.paged.PagingInfo
import me.aartikov.sesame.loop.*

internal sealed class Action<out T : Any> {
    data class LoadFirstPage(val fresh: Boolean, val reset: Boolean) : Action<Nothing>()
    object LoadMore : Action<Nothing>()
    data class Cancel(val reset: Boolean) : Action<Nothing>()
    data class MutateData<T : Any>(val transform: (List<T>) -> List<T>) : Action<T>()

    data class NewPageLoaded<T : Any>(val page: Page<T>) : Action<T>()
    object EmptyPageLoaded : Action<Nothing>()
    data class LoadingError(val throwable: Throwable) : Action<Nothing>()
}

internal sealed class Effect<out T : Any> {
    data class LoadFirstPage(val fresh: Boolean) : Effect<Nothing>()
    data class LoadNextPage<T : Any>(val pagingInfo: PagingInfo<T>) : Effect<T>()
    object CancelLoading : Effect<Nothing>()
    data class EmitEvent<T : Any>(val event: Event<T>) : Effect<T>()
}

internal typealias PagedLoadingLoop<T> = Loop<State<T>, Action<T>, Effect<T>>

internal class PagedLoadingReducer<T : Any> : Reducer<State<T>, Action<T>, Effect<T>> {

    override fun reduce(state: State<T>, action: Action<T>): Next<State<T>, Effect<T>> = when (action) {

        is Action.LoadFirstPage -> {
            if (action.reset) {
                next(
                    State.Loading,
                    Effect.LoadFirstPage(action.fresh)
                )
            } else {
                when (state) {
                    is State.Empty -> next(
                        State.Loading,
                        Effect.LoadFirstPage(action.fresh)
                    )
                    is State.Error -> next(
                        State.Loading,
                        Effect.LoadFirstPage(action.fresh)
                    )
                    is State.Data -> when (state.status) {
                        DataStatus.Normal, DataStatus.LoadingMore, DataStatus.FullData -> next(
                            State.Data(state.data, status = DataStatus.Refreshing),
                            Effect.LoadFirstPage(action.fresh)
                        )
                        else -> nothing()
                    }
                    else -> nothing()
                }
            }
        }

        is Action.LoadMore -> {
            when (state) {
                is State.Data -> when (state.status) {
                    DataStatus.Normal -> next(
                        State.Data(state.data, status = DataStatus.LoadingMore),
                        Effect.LoadNextPage(PagingInfo(state.data))
                    )
                    else -> nothing()
                }
                else -> nothing()
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
                    is State.Data -> when (state.status) {
                        DataStatus.Refreshing, DataStatus.LoadingMore -> next(
                            State.Data(state.data),
                            Effect.CancelLoading
                        )
                        else -> nothing()
                    }
                    else -> nothing()
                }
            }
        }

        is Action.NewPageLoaded -> {
            when (state) {
                is State.Loading -> next(
                    State.Data(
                        action.page.data,
                        status = if (action.page.hasNextPage) DataStatus.Normal else DataStatus.FullData
                    )
                )
                is State.Data -> when (state.status) {
                    DataStatus.Refreshing -> next(
                        State.Data(
                            action.page.data,
                            status = if (action.page.hasNextPage) DataStatus.Normal else DataStatus.FullData
                        )
                    )
                    DataStatus.LoadingMore -> next(
                        State.Data(
                            state.data + action.page.data,
                            status = if (action.page.hasNextPage) DataStatus.Normal else DataStatus.FullData
                        )
                    )
                    else -> nothing()
                }
                else -> nothing()
            }
        }

        is Action.EmptyPageLoaded -> {
            when (state) {
                is State.Loading -> next(State.Empty)
                is State.Data -> when (state.status) {
                    DataStatus.Refreshing -> next(State.Empty)
                    DataStatus.LoadingMore -> next(
                        State.Data(
                            state.data,
                            DataStatus.FullData
                        )
                    )
                    else -> nothing()
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
                is State.Data -> when (state.status) {
                    DataStatus.Refreshing, DataStatus.LoadingMore -> next(
                        State.Data(state.data),
                        Effect.EmitEvent(Event.Error(action.throwable, state))
                    )
                    else -> nothing()
                }
                else -> nothing()
            }
        }

        is Action.MutateData -> {
            when (state) {
                is State.Data -> {
                    val newData = action.transform(state.data)
                    when {
                        newData.isNotEmpty() -> next(state.copy(data = newData))
                        state.status == DataStatus.Refreshing -> next(State.Loading)
                        else -> next(State.Empty)
                    }
                }
                else -> nothing()
            }
        }
    }
}