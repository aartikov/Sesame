package me.aartikov.lib.data_binding

import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.utils.CoroutinesTestRule
import me.aartikov.lib.utils.TestLifecycleOwner
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class StateTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `receives nothing when not started`() = runBlockingTest {
        val propertyObserver = TestPropertyObserver(TestLifecycleOwner())
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        state.value++

        Assert.assertEquals(values, emptyList<Int>())
    }

    @Test
    fun `receives values after starting`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        lifecycleOwner.onStart()
        state.value++
        lifecycleOwner.onPause()
        state.value++

        Assert.assertEquals(values.size, 3)
    }

    @Test
    fun `receives only last state after stopping`() {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        lifecycleOwner.onStop()
        repeat(3) { state.value++ }
        lifecycleOwner.onStart()

        Assert.assertEquals(values, listOf(3))
    }

    @Test
    fun `receives nothing when destroyed`() = runBlockingTest {
        val lifecycleOwner = TestLifecycleOwner()
        val propertyObserver = TestPropertyObserver(lifecycleOwner)
        val state = state(0)
        val values = mutableListOf<Int>()
        with(propertyObserver) { state bind { values.add(it) } }

        lifecycleOwner.onStart()
        state.value++
        lifecycleOwner.onDestroy()
        state.value++

        Assert.assertEquals(values, listOf(0, 1))
    }
}