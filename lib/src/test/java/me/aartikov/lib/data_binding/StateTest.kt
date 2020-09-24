package me.aartikov.lib.data_binding

import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.utils.DispatchersTestRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class StateTest {

    @get:Rule
    val coroutinesTestRule = DispatchersTestRule()

    @Test
    fun `receives nothing when not started`() = runBlockingTest {
        val propertyObserver = TestPropertyObserver()
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        state.value++

        Assert.assertEquals(values, emptyList<Int>())
    }

    @Test
    fun `receives values after starting`() {
        val propertyObserver = TestPropertyObserver()
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        state.value++
        propertyObserver.propertyObserverLifecycleOwner.onPause()
        state.value++

        Assert.assertEquals(values.size, 3)
    }

    @Test
    fun `receives only last state after stopping`() {
        val propertyObserver = TestPropertyObserver()
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        repeat(3) { state.value++ }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        Assert.assertEquals(values, listOf(3))
    }

    @Test
    fun `receives nothing when destroyed`() = runBlockingTest {
        val propertyObserver = TestPropertyObserver()
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        state.value++
        propertyObserver.propertyObserverLifecycleOwner.onDestroy()
        state.value++

        Assert.assertEquals(values, listOf(0, 1))
    }
}