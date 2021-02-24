package me.aartikov.sesame.property

import androidx.lifecycle.Lifecycle
import me.aartikov.sesame.property.utils.MainDispatcherRule
import me.aartikov.sesame.property.utils.TestPropertyHost
import me.aartikov.sesame.property.utils.TestPropertyObserver
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

        propertyObserver.propertyObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        propertyHost.state++
        propertyObserver.propertyObserverLifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        propertyHost.state++
        propertyObserver.propertyObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
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

        propertyObserver.propertyObserverLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        repeat(3) { propertyHost.state++ }
        propertyObserver.propertyObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)

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

        propertyObserver.propertyObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        propertyHost.state++
        propertyObserver.propertyObserverLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        propertyHost.state++

        assertEquals(listOf(0, 1), values)
    }
}