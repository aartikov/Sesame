package me.aartikov.lib.core.loading.simple

import me.aartikov.lib.core.loading.simple.internal.LoadingEffectHandler
import me.aartikov.lib.core.loading.simple.internal.LoadingImpl

interface OrdinaryLoader<T : Any> {
    suspend fun load(fresh: Boolean): T?
}

fun <T : Any> OrdinaryLoading(
    loader: OrdinaryLoader<T>,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading(loader::load, initialState)
}

fun <T : Any> OrdinaryLoading(
    loader: suspend (fresh: Boolean) -> T?,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return LoadingImpl(LoadingEffectHandler(loader), null, initialState)
}

fun <T : Any> OrdinaryLoading(
    loader: suspend () -> T?,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading({ _ -> loader() }, initialState)
}