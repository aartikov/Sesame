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

        state.value++

        assert(values.isEmpty())
    }

    @Test
    fun `receives values after starting`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        lifecycleOwner.onStart()
        state.value++
        lifecycleOwner.onPause()
        state.value++

        assert(values.size == 3)
    }

    @Test
    fun `receives only last state after stopping`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        println(values)
        lifecycleOwner.onStop()
        repeat(3) { state.value++ }
        lifecycleOwner.onStart()

        println(values)

        assert(values.size == 1)
    }

    @Test
    fun `receives nothing when destroyed`() = runBlockingTest {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        lifecycleOwner.onStart()
        state.value++
        lifecycleOwner.onDestroy()
        state.value++

        assert(values.size == 2)
    }

    @Test
    fun `doesn't receive command when not started`() = runBlockingTest {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        command.send(0)

        assert(values.isEmpty())
    }

    @Test
    fun `receives command after starting`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        command.send(0)
        lifecycleOwner.onStart()

        assert(values.isNotEmpty())
    }

    @Test
    fun `don't receive command when stopped`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        lifecycleOwner.onStop()
        command.send(0)

        assert(values.isEmpty())
    }

    @Test
    fun `save commands after stopping`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        lifecycleOwner.onStop()
        repeat(3) { command.send(0) }
        lifecycleOwner.onStart()

        assert(values.size == 3)
    }

    @Test
    fun `save commands before starting`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        repeat(3) { command.send(0) }
        lifecycleOwner.onStart()

        assert(values.size == 3)
    }
}

private class TestLifecycleOwner : LifecycleOwner {
    private val lifecycle = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycle

    fun onCreate() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

    fun onStart() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

    fun onPause() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

    fun onResume() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

    fun onStop() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

    fun onDestroy() = lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
}

private class TestPropertyObserver(
    override val propertyObserverLifecycleOwner: LifecycleOwner
) : PropertyObserver