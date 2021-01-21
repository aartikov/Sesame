package me.aartikov.lib.core.loading.simple.internal

import me.aartikov.lib.core.loading.simple.Loading

internal fun <T : Any> State<T>.toPublicState(): Loading.State<T> = when (this) {
    State.Empty -> Loading.State.Empty
    State.Loading -> Loading.State.Loading
    is State.Error -> Loading.State.Error(this.throwable)
    is State.Data -> Loading.State.Data(this.data)
    is State.Refresh -> Loading.State.Data(this.data, refreshing = true)
}

internal fun <T : Any> Loading.State<T>.toInternalState() = when (this) {
    Loading.State.Empty -> State.Empty
    Loading.State.Loading -> State.Loading
    is Loading.State.Error -> State.Error(this.throwable)
    is Loading.State.Data -> if (this.refreshing) State.Refresh(this.data) else State.Data(this.data)
}