package me.aartikov.sesame.navigation

import androidx.lifecycle.Lifecycle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import me.aartikov.sesame.navigation.utils.MainDispatcherRule
import me.aartikov.sesame.navigation.utils.TestLifecycleOwner
import org.junit.Rule
import org.junit.Test

class NavigationMessageQueueTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `doesn't dispatch messages when lifecycleOwner is not resumed`() {
        val testMessage = object : NavigationMessage {}
        val queue = NavigationMessageQueue()
        val testDispatcher = spy<NavigationMessageDispatcher>()
        val lifecycleOwner = TestLifecycleOwner()
        val node = Any()
        queue.bind(testDispatcher, node, lifecycleOwner)

        testDispatcher.resume()
        lifecycleOwner.moveToState(Lifecycle.State.STARTED)
        queue.send(testMessage)

        verify(testDispatcher, times(0)).dispatch(any(), any())
    }

    @Test
    fun `doesn't dispatch messages when dispatcher can't handle messages`() {
        val testMessage = object : NavigationMessage {}
        val queue = NavigationMessageQueue()
        val testDispatcher = spy<NavigationMessageDispatcher>()
        val lifecycleOwner = TestLifecycleOwner()
        val node = Any()
        queue.bind(testDispatcher, node, lifecycleOwner)

        // don't call testDispatcher.resume()
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(testMessage)

        verify(testDispatcher, times(0)).dispatch(any(), any())
    }

    @Test
    fun `dispatches messages when dispatcher can handle messages and lifecycleOwner is resumed`() {
        val testMessage = object : NavigationMessage {}
        val queue = NavigationMessageQueue()
        val testDispatcher = spy<NavigationMessageDispatcher>()
        val lifecycleOwner = TestLifecycleOwner()
        val node = Any()
        queue.bind(testDispatcher, node, lifecycleOwner)

        testDispatcher.resume()
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(testMessage)

        verify(testDispatcher, times(1)).dispatch(testMessage, node)
    }

    @Test
    fun `buffers messages when dispatcher can't handle messages or lifecycleOwner is not resumed`() {
        val testMessage = object : NavigationMessage {}
        val queue = NavigationMessageQueue()
        val testDispatcher = spy<NavigationMessageDispatcher>()
        val lifecycleOwner = TestLifecycleOwner()
        val node = Any()
        queue.bind(testDispatcher, node, lifecycleOwner)

        queue.send(testMessage)
        queue.send(testMessage)
        testDispatcher.resume()
        queue.send(testMessage)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(testMessage)

        verify(testDispatcher, times(4)).dispatch(testMessage, node)
    }
}