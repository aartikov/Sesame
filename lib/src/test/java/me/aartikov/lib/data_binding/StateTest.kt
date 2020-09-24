package me.aartikov.lib.data_binding

import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.utils.DispatchersTestRule
import org.junit.Assert.assertEquals
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

        assertEquals(emptyList<Int>(), values)
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

        assertEquals(listOf(0, 1, 2), values)
    }

    @Test
    fun `receives only last state after restarting`() {
        val propertyObserver = TestPropertyObserver()
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        repeat(3) { state.value++ }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(listOf(3), values)
    }

    @Test
    fun `receives nothing when stopped`() = runBlockingTest {
        val propertyObserver = TestPropertyObserver()
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        state.value++
        propertyObserver.propertyObserverLifecycleOwner.onDestroy()
        state.value++

        assertEquals(listOf(0, 1), values)
    }
}