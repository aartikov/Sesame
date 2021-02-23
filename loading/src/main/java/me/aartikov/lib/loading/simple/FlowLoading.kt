package me.aartikov.lib.loading.simple

import kotlinx.coroutines.flow.Flow
import me.aartikov.lib.loading.simple.internal.LoadingActionSource
import me.aartikov.lib.loading.simple.internal.LoadingEffectHandler
import me.aartikov.lib.loading.simple.internal.LoadingImpl

/**
 * Data loader for [FlowLoading].
 */
interface FlowLoader<T : Any> {

    /**
     * Loads data and puts it to a cache.
     * @param fresh indicates that fresh data is required.
     * @return loaded data or null if data is missing/empty.
     */
    suspend fun load(fresh: Boolean): T?

    /**
     * Returns a flow of data from a cache.
     * @return flow of data. Nulls in a flow means that data is missing/empty.
     */
    fun observe(): Flow<T?>
}

/**
 * Creates an implementation of [Loading] that uses one method to load new data and another one to get data from a cache.
 */
fun <T : Any> FlowLoading(loader: FlowLoader<T>): Loading<T> {
    return FlowLoading(loader::load, loader::observe)
}

/**
 * Creates an implementation of [Loading] that uses one method to load new data and another one to get data from a cache.
 */
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

/**
 * Creates an implementation of [Loading] that uses one method to load new data and another one to get data from a cache.
 */
fun <T : Any> FlowLoading(
    load: suspend () -> T?,
    observe: () -> Flow<T?>
): Loading<T> {
    return FlowLoading({ _ -> load() }, observe)
}