package me.aartikov.lib.loading

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.loading.simple.*
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowLoadingTest {

    @Test
    fun `is initially empty`() {
        val loader = TestLoader(TestLoader.Result.Success("Anything"))
        val loading = FlowLoading(loader)

        assertEquals(Loading.State.Empty, loading.state)
    }

    @Test
    fun `starts loading fresh data after started`() = runBlockingTest {
        val loader = TestLoader(TestLoader.Result.Success("Anything"))
        val loading = FlowLoading(loader)

        val job = loading.startIn(this)

        assertEquals(Loading.State.EmptyLoading, loading.state)
        job.cancel()
    }

    @Test
    fun `shows fresh data when it is loaded`() = runBlockingTest {
        val loader = TestLoader(TestLoader.Result.Success("Fresh value"))
        val loading = FlowLoading(loader)

        val job = loading.startIn(this)
        delay(TestLoader.LOAD_DELAY)

        assertEquals(Loading.State.Data("Fresh value"), loading.state)
        job.cancel()
    }

    @Test
    fun `loads cached data on start and then refreshes`() = runBlockingTest {
        val loader = TestLoader(
            freshResult = TestLoader.Result.Success("Fresh value"),
            cachedResult = TestLoader.Result.Success("Cached value")
        )
        val loading = FlowLoading(loader)

        val job = loading.startIn(this)

        assertEquals(Loading.State.Refresh("Cached value"), loading.state)
        job.cancel()
    }

    @Test
    fun `loads fresh data when has cache`() = runBlockingTest {
        val loader = TestLoader(
            freshResult = TestLoader.Result.Success("Fresh value"),
            cachedResult = TestLoader.Result.Success("Cached value")
        )
        val loading = FlowLoading(loader)

        val job = loading.startIn(this)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(Loading.State.Data("Fresh value"), loading.state)
        job.cancel()
    }

    @Test
    fun `loads only cached data if it is specified`() = runBlockingTest {
        val loader = TestLoader(
            freshResult = TestLoader.Result.Success("Fresh value"),
            cachedResult = TestLoader.Result.Success("Cached value")
        )
        val loading = FlowLoading(loader)

        val job = loading.startIn(this, fresh = false)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(Loading.State.Data("Cached value"), loading.state)
        job.cancel()
    }

    @Test
    fun `updates data on cache modification`() = runBlockingTest {
        val loader = TestLoader(
            freshResult = TestLoader.Result.Success("Fresh value"),
            cachedResult = TestLoader.Result.Success("Cached value")
        )
        val loading = FlowLoading(loader)

        val job = loading.startIn(this, fresh = false)
        delay(TestLoader.LOAD_DELAY * 2)

        loader.modifyCache("New cached value")

        assertEquals(Loading.State.Data("New cached value"), loading.state)
        job.cancel()
    }

    @Test
    fun `becomes empty when cache is cleared`() = runBlockingTest {
        val loader = TestLoader(
            freshResult = TestLoader.Result.Success("Fresh value"),
            cachedResult = TestLoader.Result.Success("Cached value")
        )
        val loading = FlowLoading(loader)

        val job = loading.startIn(this, fresh = false)
        delay(TestLoader.LOAD_DELAY * 2)

        loader.clearCache()

        assertEquals(Loading.State.Empty, loading.state)
        job.cancel()
    }

    private class TestLoader(
        private val freshResult: Result,
        private val cachedResult: Result? = null
    ) : FlowLoader<String> {
        companion object {
            const val LOAD_DELAY: Long = 100
        }

        sealed class Result {
            data class Success(val value: String) : Result()
            data class Error(val throwable: Throwable) : Result()
        }

        private val eventChannel = Channel<FlowLoader.Event<String>>()

        override fun load(fresh: Boolean): Flow<FlowLoader.Event<String>> {
            return flow {
                if (cachedResult != null) {
                    emit(cachedResult.toEvent(FlowLoader.Origin.CACHE))
                }

                if (cachedResult !is Result.Success || fresh) {
                    emit(FlowLoader.Event.Loading)
                    delay(LOAD_DELAY)
                    emit(freshResult.toEvent(FlowLoader.Origin.FRESH))
                }

                emitAll(eventChannel)
            }
        }

        override suspend fun refresh() {
            eventChannel.offer(FlowLoader.Event.Loading)
            delay(LOAD_DELAY)
            eventChannel.offer(freshResult.toEvent(FlowLoader.Origin.FRESH))
        }

        fun modifyCache(data: String) {
            eventChannel.offer(FlowLoader.Event.Data(data, FlowLoader.Origin.CACHE))
        }

        fun clearCache() {
            eventChannel.offer(FlowLoader.Event.DataRemoved)
        }

        private fun Result.toEvent(origin: FlowLoader.Origin) = when (this) {
            is Result.Success -> FlowLoader.Event.Data(value, origin)
            is Result.Error -> FlowLoader.Event.Error(throwable, origin)
        }
    }
}