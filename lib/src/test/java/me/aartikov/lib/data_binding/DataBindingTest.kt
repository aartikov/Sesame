package me.aartikov.lib.data_binding

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class DataBindingTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `receives nothing when not started`() = runBlockingTest {
        val propertyObserver = TestPropertyObserver(TestLifecycleOwner())
        val state = state(0)
        val values = mutableListOf<Int>()

        with(propertyObserver) { state bind { values.add(it) } }

        assert(values.isEmpty())
    }

    @Test
    fun `receives nothing when destroyed`() = runBlockingTest {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()

        with(propertyObserver) { state bind { values.add(it) } }
        lifecycleOwner.onStart()

        lifecycleOwner.onDestroy()
        with(propertyObserver) { state bind { values.add(it) } }

        assert(values.size == 1)
    }

    @Test
    fun `receives values after starting`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()

        with(propertyObserver) { state bind { values.add(it) } }
        lifecycleOwner.onStart()

        assert(values.contains(state.value))
    }
}

private class TestLifecycleOwner : LifecycleOwner {
    private val lifecycle = LifecycleRegistry(this)

    fun onCreate() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

    fun onStart() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

    fun onResume() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

    fun onDestroy() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)


    override fun getLifecycle() = lifecycle
}

private class TestPropertyObserver(
    override val propertyObserverLifecycleOwner: LifecycleOwner
) : PropertyObserver