package me.aartikov.lib.loading.simple

import kotlinx.coroutines.flow.Flow
import me.aartikov.lib.loading.simple.internal.FlowLoadingEffectHandler
import me.aartikov.lib.loading.simple.internal.LoadingImpl

interface FlowLoader<T : Any> {

    enum class Origin {
        FRESH, CACHE
    }

    sealed class Event<out T> {
        object Loading : Event<Nothing>()   // origin = FRESH
        data class Data<T>(val data: T, val origin: Origin) : Event<T>()
        data class Error(val throwable: Throwable, val origin: Origin) : Event<Nothing>()
        object DataRemoved : Event<Nothing>() // origin = CACHE
    }

    fun load(fresh: Boolean): Flow<Event<T>>

    suspend fun refresh()
}

fun <T : Any> FlowLoading(loader: FlowLoader<T>): Loading<T> {
    return LoadingImpl(FlowLoadingEffectHandler(loader), Loading.State.Empty)
}