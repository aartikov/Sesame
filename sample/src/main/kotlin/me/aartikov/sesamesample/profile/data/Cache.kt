package me.aartikov.sesamesample.profile.data

import com.dropbox.android.external.store4.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class,
    ExperimentalTime::class,
    ExperimentalStoreApi::class
)
class Cache<T : Any>(
    expirationTimeSeconds: Int,
    maxItemCount: Int
) {

    private val store = StoreBuilder
        .from(
            fetcher = Fetcher.of { keyWithFetcher: KeyWithFetcher<T> ->
                keyWithFetcher.fetcher!!()
            }
        )
        .cachePolicy(
            MemoryPolicy.builder<KeyWithFetcher<T>, T>()
                .setExpireAfterWrite(expirationTimeSeconds.seconds)
                .setMaxSize(maxItemCount)
                .build()
        )
        .build()

    suspend fun request(key: String, fresh: Boolean, fetcher: suspend () -> T): T {
        return if (fresh) {
            store.fresh(KeyWithFetcher(key, fetcher))
        } else {
            store.get(KeyWithFetcher(key, fetcher))
        }
    }

    suspend fun clear(key: String) {
        store.clear(KeyWithFetcher(key, null))
    }

    suspend fun clearAll() {
        store.clearAll()
    }

    suspend fun set(key: String, value: T) {
        store.fresh(KeyWithFetcher(key) { value })
    }
}

private class KeyWithFetcher<T>(
    val key: String,
    val fetcher: (suspend () -> T)?
) {

    override fun equals(other: Any?): Boolean {
        return other is KeyWithFetcher<*> && key == other.key
    }

    override fun hashCode(): Int = key.hashCode()
}