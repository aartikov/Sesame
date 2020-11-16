package me.aartikov.lib.data_binding

import org.junit.Assert
import org.junit.Test

class AutorunTest {

    @Test
    fun `trigger autorun from initial state`() {
        val values = mutableListOf<Int>()
        val propertyHost = object : TestPropertyHost() {
            val state1 by state(1)
            val state2 by state(2)
            val autorun = autorun(::state1, ::state2) { val1, val2 ->
                values.add(val1 + val2)
            }
        }

        Assert.assertEquals(listOf(3), values)
    }

    @Test
    fun `trigger autorun after state changed`() {
        val values = mutableListOf<Int>()
        val propertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            val autorun = autorun(::state1) { values.add(it) }
        }

        repeat(3) { propertyHost.state1++ }

        Assert.assertEquals(listOf(1, 2, 3, 4), values)
    }

    @Test
    fun `trigger autorun after two states changed`() {
        val values = mutableListOf<Int>()
        val propertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            var state2 by state(2)

            val autorun = autorun(::state1, ::state2) { val1, val2 ->
                values.add(val1 + val2)
            }
        }

        repeat(3) { propertyHost.state1++ }
        repeat(2) { propertyHost.state2++ }

        Assert.assertEquals(listOf(3, 4, 5, 6, 7, 8), values)
    }

    @Test
    fun `trigger autorun after three states changed`() {
        val values = mutableListOf<Int>()
        val propertyHost = object : TestPropertyHost() {
            var state1 by state(1)
            var state2 by state(2)
            var state3 by state(3)

            val autorun = autorun(::state1, ::state2, ::state3) { val1, val2, val3 ->
                values.add(val1 + val2 + val3)
            }
        }

        repeat(3) { propertyHost.state1++ }
        repeat(2) { propertyHost.state2++ }
        repeat(1) { propertyHost.state3++ }

        Assert.assertEquals(listOf(6, 7, 8, 9, 10, 11, 12), values)
    }

    @Test
    fun `trigger autorun after computed`() {
        val values = mutableListOf<Int>()
        val propertyHost = object : TestPropertyHost() {
            var state by state(1)
            val value by computed(::state) { it }
            val autorun = autorun(::value) { values.add(it) }
        }

        repeat(3) { propertyHost.state++ }

        Assert.assertEquals(listOf(1, 2, 3, 4), values)
    }
}