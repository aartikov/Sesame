package me.aartikov.sesame.loading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.sesame.loading.simple.*
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowLoadingTest {

    @Test
    fun `shows cached value after initialization`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Anything"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(this, loader)

        assertEquals(Loading.State.Data("Cached value"), loading.state)
        cancelJobs()
    }

    @Test
    fun `starts loading fresh data after refresh is called`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Anything"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(this, loader)

        loading.refresh()

        assertEquals(Loading.State.Data("Cached value", refreshing = true), loading.state)
        cancelJobs()
    }

    @Test
    fun `shows fresh data when it is loaded`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Fresh value"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(this, loader)

        loading.refresh()
        delay(TestLoader.LOAD_DELAY)

        assertEquals(Loading.State.Data("Fresh value"), loading.state)
        cancelJobs()
    }

    @Test
    fun `loads only cached data if it is specified`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Fresh value"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(this, loader)

        loading.load(fresh = false)

        assertEquals(Loading.State.Data("Cached value"), loading.state)
        cancelJobs()
    }

    @Test
    fun `updates data on cache modification`() = runBlockingTest {
        val loader = TestLoader(
            TestLoader.Result.Success("Anything"),
            cachedValue = "Cached value"
        )
        val loading = FlowLoading(this, loader)

        loader.updateCache("Modified value")

        assertEquals(Loading.State.Data("Modified value"), loading.state)
        cancelJobs()
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

    private fun CoroutineScope.cancelJobs() {
        val job = coroutineContext[Job]!!
        job.children.drop(1)    // drop DeferredCoroutine from internals of runBlockingTest
            .forEach { it.cancel() }
    }
}