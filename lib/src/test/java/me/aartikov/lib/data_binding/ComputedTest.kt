package me.aartikov.lib.data_binding

import org.junit.Assert.assertEquals
import org.junit.Test

class ComputedTest {

    @Test
    fun `return computed from initial states`() {
        val testPropertyHost = object : TestPropertyHost() {
            val state1 by state(1)
            val state2 by state(2)
            val value by computed(::state1, ::state2) { val1, val2 -> val1 + val2 }
        }

        assertEquals(3, testPropertyHost.value)
    }

    @Test
    fun `update computed value after state changed`() {
        val testPropertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            val value by computed(::state1) { it }
        }

        repeat(3) { testPropertyHost.state1++ }

        println(testPropertyHost.state1)

        assertEquals(4, testPropertyHost.value)
    }

    // FIXME : works correctly when states changed only 1 time
    @Test
    fun `update computed value after two states changed`() {
        val testPropertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            var state2 by state(2)
            val value by computed(::state1, ::state2) { val1, val2 -> val1 + val2 }
        }

        repeat(3) { testPropertyHost.state2++ }

        assertEquals(6, testPropertyHost.value)
    }

    // FIXME : works correctly when states changed only 1 time
    @Test
    fun `update computed value after three states changed`() {
        val testPropertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            var state2 by state(2)
            var state3 by state(3)
            val value by computed(::state1, ::state2, ::state3) { i1, i2, i3 ->
                 i1 + i2 + i3 }
        }

        repeat(3) { testPropertyHost.state1++ }
        repeat(2) { testPropertyHost.state2++ }
        repeat(1) { testPropertyHost.state3++ }

        assertEquals(12, testPropertyHost.value)
    }

    @Test
    fun `update computed value after another computed`() {
        val testPropertyHost = object : TestPropertyHost() {
            var state by state(1)
            val value1 by computed(::state ) { it }
            val value2 by computed(::value1 ) { it + 3 }
        }

        repeat(3) { testPropertyHost.state++ }

        assertEquals(7, testPropertyHost.value2)
    }
}