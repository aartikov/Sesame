package me.aartikov.lib.loading.simple

import me.aartikov.lib.loading.simple.internal.LoadingEffectHandler
import me.aartikov.lib.loading.simple.internal.LoadingImpl

/**
 * Data loader for [OrdinaryLoading].
 */
interface OrdinaryLoader<T : Any> {
    /**
     * Loads data.
     * @param fresh indicates that fresh data is required.
     * @return loaded data or null if data is missing/empty.
     */
    suspend fun load(fresh: Boolean): T?
}

/**
 * Creates an implementation of [Loading] that uses a single suspend method to load data.
 */
fun <T : Any> OrdinaryLoading(
    loader: OrdinaryLoader<T>,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading(loader::load, initialState)
}

/**
 * Creates an implementation of [Loading] that uses a single suspend method to load data.
 */
fun <T : Any> OrdinaryLoading(
    loader: suspend (fresh: Boolean) -> T?,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return LoadingImpl(LoadingEffectHandler(loader), null, initialState)
}

/**
 * Creates an implementation of [Loading] that uses a single suspend method to load data.
 */
fun <T : Any> OrdinaryLoading(
    loader: suspend () -> T?,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading({ _ -> loader() }, initialState)
}