package me.aartikov.lib.loading.simple

import kotlinx.coroutines.flow.Flow
import me.aartikov.lib.loading.simple.internal.LoadingActionSource
import me.aartikov.lib.loading.simple.internal.LoadingEffectHandler
import me.aartikov.lib.loading.simple.internal.LoadingImpl

interface FlowLoader<T : Any> {

    suspend fun load(fresh: Boolean): T

    fun observe(): Flow<T>
}

fun <T : Any> FlowLoading(loader: FlowLoader<T>): Loading<T> {
    return LoadingImpl(
        LoadingEffectHandler(loader::load),
        LoadingActionSource(loader.observe()),
        Loading.State.Empty
    )
}