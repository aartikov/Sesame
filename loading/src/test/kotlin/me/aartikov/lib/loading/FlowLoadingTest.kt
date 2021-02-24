package me.aartikov.lib.loading

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.loading.simple.*
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowLoadingTest {

    @Test
    fun `is empty when not attached`() {
        val loader = TestLoader(
            TestLoader.Result.Success("Anything"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(loader)

        assertEquals(Loading.State.Empty, loading.state)
    }

    @Test
    fun `shows cached value after attach`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Anything"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(loader)

        val job = loading.attach(this)

        assertEquals(Loading.State.Data("Cached value"), loading.state)
        job.cancel()
    }

    @Test
    fun `starts loading fresh data after refresh is called`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Anything"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(loader)

        val job = loading.attach(this)
        loading.refresh()

        assertEquals(Loading.State.Data("Cached value", refreshing = true), loading.state)
        job.cancel()
    }

    @Test
    fun `shows fresh data when it is loaded`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Fresh value"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(loader)

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY)

        assertEquals(Loading.State.Data("Fresh value"), loading.state)
        job.cancel()
    }

    @Test
    fun `loads only cached data if it is specified`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Fresh value"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(loader)

        val job = loading.attach(this)
        loading.load(fresh = false)

        assertEquals(Loading.State.Data("Cached value"), loading.state)
        job.cancel()
    }

    @Test
    fun `updates data on cache modification`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Anything"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(loader)

        val job = loading.attach(this)
        loader.updateCache("Modified value")

        assertEquals(Loading.State.Data("Modified value"), loading.state)
        job.cancel()
    }

    private class TestLoader(private val result: Result, cachedValue: String) : FlowLoader<String> {
        companion object {
            const val LOAD_DELAY: Long = 100
        }

        sealed class Result {
            data class Success(val value: String) : Result()
            data class Error(val throwable: Throwable) : Result()
        }

        private val cache = MutableStateFlow(cachedValue)

        fun updateCache(data: String) {
            cache.value = data
        }

        override suspend fun load(fresh: Boolean): String {
            if (!fresh) {
                return cache.value
            }

            delay(LOAD_DELAY)
            return when (result) {
                is Result.Success -> result.value
                is Result.Error -> throw result.throwable
            }.also {
                updateCache(it)
            }
        }

        override fun observe(): Flow<String> = cache
    }
}