package me.aartikov.lib.property

import me.aartikov.lib.property.utils.MainDispatcherRule
import me.aartikov.lib.property.utils.TestPropertyHost
import me.aartikov.lib.property.utils.TestPropertyObserver
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CommandTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `doesn't receive command when not started`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            val command = command<Int>()
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost.command bind { values.add(it) }
        }

        with(propertyHost) {
            command(0)
        }

        assertEquals(emptyList<Int>(), values)
    }

    @Test
    fun `receives command when started`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            val command = command<Int>()
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost.command bind { values.add(it) }
        }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        with(propertyHost) {
            command(0)
        }
        propertyObserver.propertyObserverLifecycleOwner.onResume()
        with(propertyHost) {
            command(1)
        }
        propertyObserver.propertyObserverLifecycleOwner.onPause()
        with(propertyHost) {
            command(2)
        }

        assertEquals(listOf(0, 1, 2), values)
    }

    @Test
    fun `doesn't receive command when stopped`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            val command = command<Int>()
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost.command bind { values.add(it) }
        }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        with(propertyHost) {
            command(0)
        }

        assertEquals(emptyList<Int>(), values)
    }

    @Test
    fun `buffers commands when stopped`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            val command = command<Int>()
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost.command bind { values.add(it) }
        }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        with(propertyHost) {
            repeat(3) { command(0) }
        }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(listOf(0, 0, 0), values)
    }

    @Test
    fun `buffers commands before starting`() {
        val propertyObserver = TestPropertyObserver()
        val propertyHost = object : TestPropertyHost() {
            val command = command<Int>()
        }
        val values = mutableListOf<Int>()
        with(propertyObserver) {
            propertyHost.command bind { values.add(it) }
        }

        with(propertyHost) {
            repeat(3) { command(0) }
        }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(listOf(0, 0, 0), values)
    }
}