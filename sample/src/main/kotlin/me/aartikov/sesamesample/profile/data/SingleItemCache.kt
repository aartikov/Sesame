package me.aartikov.sesamesample.profile.data

class SingleItemCache<T : Any>(expirationTimeSeconds: Int) {

    companion object {
        private const val KEY = "key"
    }

    private val cache = Cache<T>(expirationTimeSeconds, maxItemCount = 1)

    suspend fun request(fresh: Boolean, fetcher: suspend () -> T): T {
        return cache.request(KEY, fresh, fetcher)
    }

    suspend fun clear() {
        cache.clear(KEY)
    }

    suspend fun set(value: T) {
        cache.set(KEY, value)
    }
}