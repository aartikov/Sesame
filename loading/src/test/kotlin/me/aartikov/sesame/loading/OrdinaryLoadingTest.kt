package me.aartikov.sesame.loading

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.sesame.loading.OrdinaryLoadingTest.TestLoader.Result
import me.aartikov.sesame.loading.simple.*
import me.aartikov.sesame.loading.simple.Loading.Event
import me.aartikov.sesame.loading.simple.Loading.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class OrdinaryLoadingTest {

    @Test
    fun `is initially empty`() {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(loader)

        assertEquals(State.Empty, loading.state)
    }

    @Test
    fun `is empty after attach`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)

        assertEquals(State.Empty, loading.state)
        job.cancel()
    }

    @Test
    fun `fails if attach is called twice`() {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(loader)

        assertThrows(IllegalStateException::class.java) {
            runBlockingTest {
                loading.attach(this)
                loading.attach(this)
            }
        }
    }

    @Test
    fun `starts loading when refresh is called`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)
        loading.refresh()

        assertEquals(State.Loading, loading.state)
        assertEquals(loader.callCount, 1)
        job.cancel()
    }

    @Test
    fun `is empty when loaded data is empty`() = runBlockingTest {
        val loader = suspend { emptyList<String>() }
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Empty, loading.state)
        job.cancel()
    }

    @Test
    fun `treat null as empty`() = runBlockingTest {
        val loader = suspend { null }
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Empty, loading.state)
        job.cancel()
    }

    @Test
    fun `shows data when it is loaded`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Value"), loading.state)
        job.cancel()
    }

    @Test
    fun `shows error when loader failed`() = runBlockingTest {
        val loader = TestLoader(Result.Error(LoadingFailedException()))
        val loading = OrdinaryLoading(loader)
        val events = mutableListOf<Event>()

        val eventsJob = launch {
            loading.eventFlow.toList(events)
        }
        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Error(LoadingFailedException()), loading.state)
        assertEquals(listOf(Event.Error(LoadingFailedException(), hasData = false)), events)
        job.cancel()
        eventsJob.cancel()
    }

    @Test
    fun `shows previous data during refresh`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(loader, initialState = State.Data("Previous value"))

        val job = loading.attach(this)
        loading.refresh()

        assertEquals(State.Data("Previous value", refreshing = true), loading.state)
        job.cancel()
    }

    @Test
    fun `replaces previous data after refresh`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(loader, initialState = State.Data("Previous value"))

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Value"), loading.state)
        job.cancel()
    }

    @Test
    fun `leaves previous data and shows error when refresh failed`() = runBlockingTest {
        val loader = TestLoader(Result.Error(LoadingFailedException()))
        val loading = OrdinaryLoading(loader, initialState = State.Data("Previous value"))
        val events = mutableListOf<Event>()

        val job = loading.attach(this)
        val eventsJob = launch {
            loading.eventFlow.toList(events)
        }
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Previous value"), loading.state)
        assertEquals(listOf(Event.Error(LoadingFailedException(), hasData = true)), events)
        job.cancel()
        eventsJob.cancel()
    }

    @Test
    fun `does not call loader twice if already loading`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)
        delay(TestLoader.LOAD_DELAY / 2)
        loading.refresh()

        assertEquals(1, loader.callCount)
        job.cancel()
    }

    @Test
    fun `loads cached data when it is specified`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)
        loading.load(fresh = false)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Value (cached)"), loading.state)
        job.cancel()
    }

    @Test
    fun `starts loading after restart`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(loader, initialState = State.Data("Value"))

        val job = loading.attach(this)
        loading.restart()

        assertEquals(State.Loading, loading.state)
        job.cancel()
    }

    @Test
    fun `loads new data after restart`() = runBlockingTest {
        var resultValue = "First"
        val loader = TestLoader { Result.Success(resultValue) }
        val loading = OrdinaryLoading(loader)

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY / 2)
        resultValue = "Second"
        loading.restart()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Second"), loading.state)
        job.cancel()
    }

    @Test
    fun `cancels loading and leaves previous data after cancel is called`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(loader, initialState = State.Data("Previous value"))

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY / 2)
        loading.cancel()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Previous value"), loading.state)
        job.cancel()
    }

    @Test
    fun `cancels loading and clears data after reset is called`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(loader, initialState = State.Data("Previous value"))

        val job = loading.attach(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY / 2)
        loading.reset()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Empty, loading.state)
        job.cancel()
    }

    private class TestLoader(private val resultProvider: () -> Result) : OrdinaryLoader<String> {

        constructor(result: Result) : this({ result })

        companion object {
            const val LOAD_DELAY: Long = 100
        }

        sealed class Result {
            data class Success(val value: String) : Result()
            data class Error(val throwable: Throwable) : Result()
        }

        var callCount = 0
            private set

        override suspend fun load(fresh: Boolean): String {
            callCount++
            delay(LOAD_DELAY)
            return when (val result = resultProvider.invoke()) {
                is Result.Success -> if (fresh) result.value else "${result.value} (cached)"
                is Result.Error -> throw result.throwable
            }
        }
    }
}