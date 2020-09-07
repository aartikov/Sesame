package me.aartikov.androidarchitecture.base

import com.dropbox.android.external.store4.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import me.aartikov.lib.loading.simple.FlowLoader
import me.aartikov.lib.loading.simple.FlowLoading
import me.aartikov.lib.loading.simple.Loading
import kotlin.coroutines.cancellation.CancellationException

private class StoreFlowLoader<Key : Any, T : Any>(
    private val store: Store<Key, T>,
    private val key: Key
) : FlowLoader<T> {

    private val eventChannel = Channel<FlowLoader.Event<T>>()

    override fun load(fresh: Boolean): Flow<FlowLoader.Event<T>> {
        return merge(
            store.stream(StoreRequest.cached(key, fresh))
                .map { storeResponseToEvent(it) },
            eventChannel.consumeAsFlow()
        )
    }

    override suspend fun refresh() {
        try {
            eventChannel.offer(FlowLoader.Event.Loading)
            store.fresh(key)
        } catch (e: Exception) {
            if (e != CancellationException()) {
                eventChannel.offer(FlowLoader.Event.Error(e, FlowLoader.Origin.FRESH))
            }
        }
    }

    private fun storeResponseToEvent(response: StoreResponse<T>) = when (response) {
        is StoreResponse.Loading -> FlowLoader.Event.Loading
        is StoreResponse.Data -> FlowLoader.Event.Data(response.value, mapOrigin(response.origin))
        is StoreResponse.Error.Exception -> FlowLoader.Event.Error(response.error, mapOrigin(response.origin))
        is StoreResponse.Error.Message ->
            FlowLoader.Event.Error(RuntimeException(response.message), mapOrigin(response.origin))
        is StoreResponse.NoNewData -> FlowLoader.Event.DataRemoved  // TODO:???
    }

    private fun mapOrigin(origin: ResponseOrigin) = when (origin) {
        ResponseOrigin.Cache -> FlowLoader.Origin.CACHE
        ResponseOrigin.SourceOfTruth -> FlowLoader.Origin.CACHE
        ResponseOrigin.Fetcher -> FlowLoader.Origin.FRESH
    }
}

fun <Key : Any, T : Any> StoreLoading(store: Store<Key, T>, key: Key): Loading<T> {
    val loader = StoreFlowLoader(store, key)
    return FlowLoading(loader)
}

fun <T : Any> StoreLoading(store: Store<Unit, T>): Loading<T> {
    val loader = StoreFlowLoader(store, Unit)
    return FlowLoading(loader)
}