package me.aartikov.sesame.loading.simple

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.loading.simple.internal.LoadingEffectHandler
import me.aartikov.sesame.loading.simple.internal.LoadingImpl

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
    scope: CoroutineScope,
    loader: OrdinaryLoader<T>,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading(scope, loader::load, initialState)
}

/**
 * Creates an implementation of [Loading] that uses a single suspend method to load data.
 */
fun <T : Any> OrdinaryLoading(
    scope: CoroutineScope,
    load: suspend (fresh: Boolean) -> T?,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return LoadingImpl(scope, LoadingEffectHandler(load), null, initialState)
}

/**
 * Creates an implementation of [Loading] that uses a single suspend method to load data.
 */
fun <T : Any> OrdinaryLoading(
    scope: CoroutineScope,
    load: suspend () -> T?,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading(scope, { _ -> load() }, initialState)
}