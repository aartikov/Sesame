package me.aartikov.lib.core.loading.simple

import kotlinx.coroutines.flow.Flow
import me.aartikov.lib.core.loading.simple.internal.LoadingActionSource
import me.aartikov.lib.core.loading.simple.internal.LoadingEffectHandler
import me.aartikov.lib.core.loading.simple.internal.LoadingImpl

interface FlowLoader<T : Any> {

    suspend fun load(fresh: Boolean): T?

    fun observe(): Flow<T?>
}

fun <T : Any> FlowLoading(loader: FlowLoader<T>): Loading<T> {
    return FlowLoading(loader::load, loader::observe)
}

fun <T : Any> FlowLoading(
    load: suspend (fresh: Boolean) -> T?,
    observe: () -> Flow<T?>
): Loading<T> {
    return LoadingImpl(
        LoadingEffectHandler(load),
        LoadingActionSource(observe()),
        Loading.State.Empty
    )
}

fun <T : Any> FlowLoading(
    load: suspend () -> T?,
    observe: () -> Flow<T?>
): Loading<T> {
    return FlowLoading({ _ -> load() }, observe)
}