package me.aartikov.lib.loading.paged.internal

import me.aartikov.lib.loading.paged.PagedLoading.Event
import me.aartikov.lib.loading.paged.PagedLoading.State
import me.aartikov.lib.loading.paged.PagingInfo
import me.aartikov.lib.state_machine.Next
import me.aartikov.lib.state_machine.Reducer
import me.aartikov.lib.state_machine.StateMachine
import me.aartikov.lib.state_machine.nothing

internal sealed class Action<out T> {
    data class Load(val fresh: Boolean) : Action<Nothing>()
    object Refresh : Action<Nothing>()
    object LoadMore : Action<Nothing>()

    data class NewPage<T>(val data: List<T>) : Action<T>()
    object EmptyPage : Action<Nothing>()
    data class Error(val throwable: Throwable) : Action<Nothing>()
}

internal sealed class Effect<T> {
    data class LoadFirstPage(val fresh: Boolean) : Effect<Nothing>()
    data class LoadNextPage<T>(val pagingInfo: PagingInfo<T>) : Effect<T>()
    data class EmitEvent(val event: Event) : Effect<Nothing>()
}

internal typealias PagedLoadingStateMachine<T> = StateMachine<State<T>, Action<T>, Effect<T>>

internal class PagedLoadingReducer<T> : Reducer<State<T>, Action<T>, Effect<T>> {

    override fun reduce(state: State<T>, action: Action<T>): Next<State<T>, Effect<T>> =
        when (action) {
            else -> nothing()
        }
}