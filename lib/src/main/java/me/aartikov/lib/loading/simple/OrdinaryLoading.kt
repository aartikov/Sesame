package me.aartikov.lib.loading.simple

import me.aartikov.lib.loading.simple.internal.LoadingImpl
import me.aartikov.lib.loading.simple.internal.OrdinaryLoadingEffectHandler

interface OrdinaryLoader<T : Any> {
    suspend fun load(fresh: Boolean): T
}

fun <T : Any> OrdinaryLoading(
    loader: OrdinaryLoader<T>,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return LoadingImpl(OrdinaryLoadingEffectHandler(loader), initialState)
}

fun <T : Any> OrdinaryLoading(
    loader: suspend (fresh: Boolean) -> T,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading(
        object : OrdinaryLoader<T> {
            override suspend fun load(fresh: Boolean): T {
                return loader(fresh)
            }
        },
        initialState
    )
}

fun <T : Any> OrdinaryLoading(
    loader: suspend () -> T,
    initialState: Loading.State<T> = Loading.State.Empty
): Loading<T> {
    return OrdinaryLoading(
        object : OrdinaryLoader<T> {
            override suspend fun load(fresh: Boolean): T {
                return loader()
            }
        },
        initialState
    )
}