package me.aartikov.lib.data_binding

import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.utils.DispatchersTestRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CommandTest {

    @get:Rule
    val coroutinesTestRule = DispatchersTestRule()

    @Test
    fun `doesn't receive command when not started`() = runBlockingTest {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        command.send(0)

        assertEquals(values, emptyList<Int>())
    }

    @Test
    fun `receives command after starting`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        command.send(0)
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(values, listOf(0))
    }

    @Test
    fun `doesn't receive command when stopped`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        command.send(0)

        assertEquals(values, emptyList<Int>())
    }

    @Test
    fun `saves commands after stopping`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        repeat(3) { command.send(0) }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(values, listOf(0, 0, 0))
    }

    @Test
    fun `saves commands before starting`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        repeat(3) { command.send(0) }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(values, listOf(0, 0, 0))
    }
}