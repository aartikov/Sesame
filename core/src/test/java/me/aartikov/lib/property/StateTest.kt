package me.aartikov.lib.property

import me.aartikov.lib.utils.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StateTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `receives nothing when not started`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            var state by state(0)
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost::state bind { values.add(it) }
        }

        propertyHost.state++

        assertEquals(emptyList<Int>(), values)
    }

    @Test
    fun `receives values when started`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            var state by state(0)
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost::state bind { values.add(it) }
        }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        propertyHost.state++
        propertyObserver.propertyObserverLifecycleOwner.onResume()
        propertyHost.state++
        propertyObserver.propertyObserverLifecycleOwner.onPause()
        propertyHost.state++

        assertEquals(listOf(0, 1, 2, 3), values)
    }

    @Test
    fun `receives only last state when restarted`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            var state by state(0)
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost::state bind { values.add(it) }
        }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        repeat(3) { propertyHost.state++ }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(listOf(3), values)
    }

    @Test
    fun `receives nothing when stopped`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            var state by state(0)
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost::state bind { values.add(it) }
        }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        propertyHost.state++
        propertyObserver.propertyObserverLifecycleOwner.onStop()
        propertyHost.state++

        assertEquals(listOf(0, 1), values)
    }
}