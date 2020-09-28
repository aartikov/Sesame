package me.aartikov.lib.data_binding

import me.aartikov.lib.utils.DispatchersTestRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CommandTest {

    @get:Rule
    val dispatchersTestRule = DispatchersTestRule()

    @Test
    fun `doesn't receive command when not started`()  {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        command.send(0)

        assertEquals(emptyList<Int>(), values)
    }

    @Test
    fun `receives command when started`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStart()
        command.send(0)
        propertyObserver.propertyObserverLifecycleOwner.onResume()
        command.send(1)
        propertyObserver.propertyObserverLifecycleOwner.onPause()
        command.send(2)

        assertEquals(listOf(0, 1, 2), values)
    }

    @Test
    fun `doesn't receive command when stopped`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        command.send(0)

        assertEquals(emptyList<Int>(), values)
    }

    @Test
    fun `saves commands when stopped`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        propertyObserver.propertyObserverLifecycleOwner.onStop()
        repeat(3) { command.send(0) }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(listOf(0, 0, 0), values)
    }

    @Test
    fun `saves commands before starting`() {
        val propertyObserver = TestPropertyObserver()
        val command = command<Int>()
        val values = mutableListOf<Int>()
        with(propertyObserver) { command bind { values.add(it) } }

        repeat(3) { command.send(0) }
        propertyObserver.propertyObserverLifecycleOwner.onStart()

        assertEquals(listOf(0, 0, 0), values)
    }
}