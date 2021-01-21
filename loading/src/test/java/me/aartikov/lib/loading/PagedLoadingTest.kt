package me.aartikov.lib.loading

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.loading.PagedLoadingTest.TestLoader.Result
import me.aartikov.lib.loading.paged.*
import me.aartikov.lib.loading.paged.PagedLoading.*
import org.junit.Assert.assertEquals
import org.junit.Test

class PagedLoadingTest {

    @Test
    fun `is initially empty`() {
        val loader = TestLoader(Result.Success(listOf("Anything")))
        val loading = PagedLoading(loader)

        assertEquals(State.Empty, loading.state)
    }

    @Test
    fun `starts loading after started`() = runBlockingTest {
        val loader = TestLoader(Result.Success(listOf("Anything")))
        val loading = PagedLoading(loader)

        val job = loading.startIn(this)

        assertEquals(State.Loading, loading.state)
        assertEquals(loader.loadFirstPageCallCount, 1)
        assertEquals(loader.loadNextPageCallCount, 0)
        job.cancel()
    }

    @Test
    fun `is empty when loaded data is empty`() = runBlockingTest {
        val loader = TestLoader(Result.Success(emptyList()))
        val loading = PagedLoading(loader)

        val job = loading.startIn(this)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Empty, loading.state)
        job.cancel()
    }

    @Test
    fun `shows data when it is loaded`() = runBlockingTest {
        val loader = TestLoader(Result.Success(listOf("Value1", "Value2")))
        val loading = PagedLoading(loader)

        val job = loading.startIn(this)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(1, listOf("Value1", "Value2")), loading.state)
        job.cancel()
    }

    @Test
    fun `shows error when loading failed`() = runBlockingTest {
        val loader = TestLoader(Result.Error(LoadingFailedException()))
        val loading = PagedLoading(loader)
        val events = mutableListOf<Event>()

        val eventsJob = launch {
            loading.eventFlow.toList(events)
        }
        val job = loading.startIn(this)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Error(LoadingFailedException()), loading.state)
        assertEquals(listOf(Event.Error(LoadingFailedException(), hasData = false)), events)
        job.cancel()
        eventsJob.cancel()
    }

    @Test
    fun `shows previous data during refresh`() = runBlockingTest {
        val loader = TestLoader(Result.Success(listOf("Value1", "Value2")))
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Previous value1", "Previous value2")))

        val job = loading.startIn(this)
        loading.refresh()

        assertEquals(State.Data(1, listOf("Previous value1", "Previous value2"), DataStatus.REFRESHING), loading.state)
        job.cancel()
    }

    @Test
    fun `replaces previous data after refresh`() = runBlockingTest {
        val loader = TestLoader(Result.Success(listOf("Value1", "Value2")))
        val loading = PagedLoading(
            loader,
            initialState = State.Data(2, listOf("Previous value1", "Previous value2", "Previous value3"))
        )

        val job = loading.startIn(this)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(1, listOf("Value1", "Value2")), loading.state)
        job.cancel()
    }

    @Test
    fun `leaves previous data and shows error when refresh failed`() = runBlockingTest {
        val loader = TestLoader(Result.Error(LoadingFailedException()))
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Previous value1", "Previous value2")))
        val events = mutableListOf<Event>()

        val job = loading.startIn(this)
        val eventsJob = launch {
            loading.eventFlow.toList(events)
        }
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(1, listOf("Previous value1", "Previous value2")), loading.state)
        assertEquals(listOf(Event.Error(LoadingFailedException(), hasData = true)), events)
        job.cancel()
        eventsJob.cancel()
    }

    @Test
    fun `does not call loader twice if already loading`() = runBlockingTest {
        val loader = TestLoader(Result.Success(listOf("Value1", "Value2")))
        val loading = PagedLoading(loader)

        val job = loading.startIn(this)
        delay(TestLoader.LOAD_DELAY / 2)
        loading.refresh()

        assertEquals(1, loader.loadFirstPageCallCount)
        job.cancel()
    }

    @Test
    fun `loads not fresh data when it is specified`() = runBlockingTest {
        val loader = TestLoader(Result.Success(listOf("Value1", "Value2")))
        val loading = PagedLoading(loader)

        val job = loading.startIn(this, fresh = false)
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(1, listOf("Value1 (cached)", "Value2 (cached)")), loading.state)
        job.cancel()
    }

    @Test
    fun `shows loading more progress`() = runBlockingTest {
        val loader = TestLoader(Result.Success(listOf("Value3", "Value4")))
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Value1", "Value2")))

        val job = loading.startIn(this)
        loading.loadMore()
        delay(TestLoader.LOAD_DELAY / 2)

        assertEquals(State.Data(1, listOf("Value1", "Value2"), DataStatus.LOADING_MORE), loading.state)
        job.cancel()
    }

    @Test
    fun `loads next page`() = runBlockingTest {
        val loader = TestLoader(
            firstPageResult = Result.Success(listOf("Anything")),
            nextPageResult = { pagingInfo ->
                val pageSize = 2
                val itemIndex1 = pageSize * pagingInfo.loadedPageCount + 1
                val itemIndex2 = itemIndex1 + 1
                Result.Success(listOf("Value$itemIndex1", "Value$itemIndex2"))
            }
        )
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Value1", "Value2")))

        val job = loading.startIn(this)
        loading.loadMore()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(2, listOf("Value1", "Value2", "Value3", "Value4")), loading.state)
        job.cancel()
    }

    @Test
    fun `shows error when loading more failed`() = runBlockingTest {
        val loader = TestLoader(
            firstPageResult = Result.Success(listOf("Anything")),
            nextPageResult = { Result.Error(LoadingFailedException()) }
        )
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Value1", "Value2")))
        val events = mutableListOf<Event>()

        val job = loading.startIn(this)
        val eventsJob = launch {
            loading.eventFlow.toList(events)
        }
        loading.loadMore()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(1, listOf("Value1", "Value2")), loading.state)
        assertEquals(listOf(Event.Error(LoadingFailedException(), hasData = true)), events)
        job.cancel()
        eventsJob.cancel()
    }

    @Test
    fun `stop paging when end of data is reached`() = runBlockingTest {
        val loader = TestLoader(
            firstPageResult = Result.Success(listOf("Anything")),
            nextPageResult = { Result.Success(emptyList()) }
        )
        val loading = PagedLoading(loader, initialState = State.Data(2, listOf("Value1", "Value2", "Value3")))

        val job = loading.startIn(this)
        loading.loadMore()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(2, listOf("Value1", "Value2", "Value3"), DataStatus.FULL_DATA), loading.state)
        job.cancel()
    }


    @Test
    fun `cancel loading more when refresh called`() = runBlockingTest {
        val loader = TestLoader(
            firstPageResult = Result.Success(listOf("Value1", "Value2")),
            nextPageResult = { Result.Success(listOf("Value3", "Value4")) }
        )
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Previous value1", "Previous value2")))

        val job = loading.startIn(this)
        loading.loadMore()
        delay(TestLoader.LOAD_DELAY / 2)
        loading.refresh()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(1, listOf("Value1", "Value2")), loading.state)
        job.cancel()
    }

    @Test
    fun `starts loading after restart`() = runBlockingTest {
        val loader = TestLoader(
            firstPageResult = Result.Success(listOf("Anything"))
        )
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Value1", "Value2")))

        val job = loading.startIn(this)
        loading.restart()

        assertEquals(State.Loading, loading.state)
        job.cancel()
    }

    @Test
    fun `loads new data after restart`() = runBlockingTest {
        val loader = TestLoader(
            firstPageResult = Result.Success(listOf("Value1", "Value2")),
            nextPageResult = { Result.Success(listOf("Value3", "Value4")) }
        )
        val loading = PagedLoading(loader, initialState = State.Data(1, listOf("Previous value1", "Previous value2")))

        val job = loading.startIn(this)
        loading.loadMore()
        delay(TestLoader.LOAD_DELAY / 2)
        loading.restart()
        delay(TestLoader.LOAD_DELAY * 2)

        assertEquals(State.Data(1, listOf("Value1", "Value2")), loading.state)
        job.cancel()
    }

    private class TestLoader(
        private val firstPageResult: Result,
        private val nextPageResult: (PagingInfo<String>) -> Result = { Result.Success(emptyList()) }
    ) : PagedLoader<String> {
        companion object {
            const val LOAD_DELAY: Long = 100
        }

        sealed class Result {
            data class Success(val values: List<String>) : Result()

            data class Error(val throwable: Throwable) : Result()
        }

        var loadFirstPageCallCount = 0
            private set

        var loadNextPageCallCount = 0
            private set

        override suspend fun loadFirstPage(fresh: Boolean): List<String> {
            loadFirstPageCallCount++
            return load(firstPageResult, fresh)
        }

        override suspend fun loadNextPage(pagingInfo: PagingInfo<String>): List<String> {
            loadNextPageCallCount++
            return load(nextPageResult(pagingInfo), true)
        }

        private suspend fun load(result: Result, fresh: Boolean): List<String> {
            delay(LOAD_DELAY)
            return when (result) {
                is Result.Success -> if (fresh) result.values else result.values.map { "$it (cached)" }
                is Result.Error -> throw result.throwable
            }
        }
    }
}