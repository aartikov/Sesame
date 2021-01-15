package me.aartikov.lib.data_binding

import me.aartikov.lib.utils.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ComputedTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `returns computed from initial states`() {
        val propertyHost = object : TestPropertyHost() {
            val state1 by state(1)
            val state2 by state(2)
            val value by computed(::state1, ::state2) { val1, val2 -> val1 + val2 }
        }

        assertEquals(3, propertyHost.value)
    }

    @Test
    fun `updates computed value after state changed`() {
        val propertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            val value by computed(::state1) { it }
        }

        repeat(3) { propertyHost.state1++ }

        assertEquals(4, propertyHost.value)
    }

    @Test
    fun `updates computed value after two states changed`() {
        val propertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            var state2 by state(2)
            val value by computed(::state1, ::state2) { val1, val2 -> val1 + val2 }
        }

        repeat(3) { propertyHost.state1++ }
        repeat(2) { propertyHost.state2++ }

        assertEquals(8, propertyHost.value)
    }

    @Test
    fun `updates computed value after three states changed`() {
        val propertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            var state2 by state(2)
            var state3 by state(3)
            val value by computed(::state1, ::state2, ::state3) { i1, i2, i3 ->
                 i1 + i2 + i3 }
        }

        repeat(3) { propertyHost.state1++ }
        repeat(2) { propertyHost.state2++ }
        repeat(1) { propertyHost.state3++ }

        assertEquals(12, propertyHost.value)
    }

    @Test
    fun `updates computed value after another computed`() {
        val propertyHost = object : TestPropertyHost() {
            var state by state(1)
            val value1 by computed(::state) { it }
            val value2 by computed(::value1) { it + 3 }
        }

        repeat(3) { propertyHost.state++ }

        assertEquals(7, propertyHost.value2)
    }

    @Test
    fun `updates computed value in binding when started`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            var state by state(0)
            val value by computed(::state) { it }
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) { propertyHost::value bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        repeat(3) { propertyHost.state++ }

        assertEquals(listOf(0, 1, 2, 3), values)
    }

    @Test
    fun `doesn't update computed value in binding when stopped`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            var state by state(0)
            val value by computed(::state) { it }
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) { propertyHost::value bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        propertyHost.state++
        propertyObserver.propertyObserverLifecycleOwner.onStop()
        repeat(3) { propertyHost.state++ }

        assertEquals(listOf(0, 1), values)
    }
}