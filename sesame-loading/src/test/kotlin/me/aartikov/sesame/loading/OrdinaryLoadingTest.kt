package me.aartikov.sesame.loading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.sesame.loading.OrdinaryLoadingTest.TestLoader.Result
import me.aartikov.sesame.loading.simple.*
import me.aartikov.sesame.loading.simple.Loading.Event
import me.aartikov.sesame.loading.simple.Loading.State
import org.junit.Assert.assertEquals
import org.junit.Test

class OrdinaryLoadingTest {

    @Test
    fun `is initially empty`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(this, loader)

        assertEquals(State.Empty, loading.state)
        cancelJobs()
    }

    @Test
    fun `starts loading when refresh is called`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(this, loader)

        loading.refresh()

        assertEquals(State.Loading, loading.state)
        assertEquals(loader.callCount, 1)
        cancelJobs()
    }

    @Test
    fun `is empty when loaded data is empty`() = runBlockingTest {
        val loading = OrdinaryLoading(this, { emptyList<String>() })

        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Empty, loading.state)
        cancelJobs()
    }

    @Test
    fun `treat null as empty`() = runBlockingTest {
        val loading = OrdinaryLoading(this, { null })

        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Empty, loading.state)
        cancelJobs()
    }

    @Test
    fun `shows data when it is loaded`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(this, loader)

        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Value"), loading.state)
        cancelJobs()
    }

    @Test
    fun `shows error when loader failed`() = runBlockingTest {
        val loader = TestLoader(Result.Error(LoadingFailedException()))
        val loading = OrdinaryLoading(this, loader)
        val events = mutableListOf<Event<String>>()

        launch {
            loading.eventFlow.toList(events)
        }
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Error(LoadingFailedException()), loading.state)
        assertEquals(listOf(Event.Error(LoadingFailedException(), State.Loading)), events)
        cancelJobs()
    }

    @Test
    fun `shows previous data during refresh`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(this, loader, initialState = State.Data("Previous value"))

        loading.refresh()

        assertEquals(State.Data("Previous value", refreshing = true), loading.state)
        cancelJobs()
    }

    @Test
    fun `replaces previous data after refresh`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(this, loader, initialState = State.Data("Previous value"))

        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Value"), loading.state)
        cancelJobs()
    }

    @Test
    fun `leaves previous data and shows error when refresh failed`() = runBlockingTest {
        val loader = TestLoader(Result.Error(LoadingFailedException()))
        val loading = OrdinaryLoading(this, loader, initialState = State.Data("Previous value"))
        val events = mutableListOf<Event<String>>()

        launch {
            loading.eventFlow.toList(events)
        }
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Previous value"), loading.state)
        val expectedStateDuringLoading = State.Data("Previous value", refreshing = true)
        assertEquals(listOf(Event.Error(LoadingFailedException(), expectedStateDuringLoading)), events)
        cancelJobs()
    }

    @Test
    fun `does not call loader twice if already loading`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(this, loader)

        delay(TestLoader.LOAD_DELAY / 2)
        loading.refresh()

        assertEquals(1, loader.callCount)
        cancelJobs()
    }

    @Test
    fun `loads cached data when it is specified`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(this, loader)

        loading.load(fresh = false)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Value (cached)"), loading.state)
        cancelJobs()
    }

    @Test
    fun `starts loading after restart`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(this, loader, initialState = State.Data("Value"))

        loading.restart()

        assertEquals(State.Loading, loading.state)
        cancelJobs()
    }

    @Test
    fun `loads new data after restart`() = runBlockingTest {
        var resultValue = "First"
        val loader = TestLoader { Result.Success(resultValue) }
        val loading = OrdinaryLoading(this, loader)

        loading.refresh()
        delay(TestLoader.LOAD_DELAY / 2)
        resultValue = "Second"
        loading.restart()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Second"), loading.state)
        cancelJobs()
    }

    @Test
    fun `cancels loading and leaves previous data after cancel is called`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(this, loader, initialState = State.Data("Previous value"))

        loading.refresh()
        delay(TestLoader.LOAD_DELAY / 2)
        loading.cancel()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data("Previous value"), loading.state)
        cancelJobs()
    }

    @Test
    fun `cancels loading and clears data after reset is called`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Value"))
        val loading = OrdinaryLoading(this, loader, initialState = State.Data("Previous value"))

        loading.refresh()
        delay(TestLoader.LOAD_DELAY / 2)
        loading.reset()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Empty, loading.state)
        cancelJobs()
    }

    @Test
    fun `shows new data when it was mutated`() = runBlockingTest {
        val loader = TestLoader(Result.Success("Anything"))
        val loading = OrdinaryLoading(this, loader, initialState = State.Data("Value"))

        loading.mutateData { "Mutated $it" }

        assertEquals(State.Data("Mutated Value"), loading.state)
        cancelJobs()
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

    private fun CoroutineScope.cancelJobs() {
        val job = coroutineContext[Job]!!
        job.children.drop(1)    // drop DeferredCoroutine from internals of runBlockingTest
            .forEach { it.cancel() }
    }
}