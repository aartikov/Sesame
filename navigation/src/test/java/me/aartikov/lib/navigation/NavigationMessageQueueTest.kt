package me.aartikov.lib.navigation

import androidx.lifecycle.Lifecycle
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import me.aartikov.lib.navigation.utils.MainDispatcherRule
import me.aartikov.lib.navigation.utils.TestLifecycleOwner
import org.junit.Rule
import org.junit.Test

class NavigationMessageQueueTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `doesn't dispatches messages when not resumed`() {
        val testMessage = object : NavigationMessage {}
        val queue = NavigationMessageQueue()
        val testDispatcher = mock<NavigationMessageDispatcher>()
        val lifecycleOwner = TestLifecycleOwner()
        val node = Any()
        queue.bind(lifecycleOwner, testDispatcher, node)

        lifecycleOwner.moveToState(Lifecycle.State.STARTED)
        queue.send(testMessage)
        verifyZeroInteractions(testDispatcher)
    }

    @Test
    fun `dispatches messages when resumed`() {
        val testMessage = object : NavigationMessage {}
        val queue = NavigationMessageQueue()
        val testDispatcher = mock<NavigationMessageDispatcher>()
        val lifecycleOwner = TestLifecycleOwner()
        val node = Any()
        queue.bind(lifecycleOwner, testDispatcher, node)

        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(testMessage)
        verify(testDispatcher, times(1)).dispatch(testMessage, node)
    }

    @Test
    fun `buffers messages`() {
        val testMessage = object : NavigationMessage {}
        val queue = NavigationMessageQueue()
        val testDispatcher = mock<NavigationMessageDispatcher>()
        val lifecycleOwner = TestLifecycleOwner()
        val node = Any()
        queue.bind(lifecycleOwner, testDispatcher, node)

        lifecycleOwner.moveToState(Lifecycle.State.STARTED)
        queue.send(testMessage)
        queue.send(testMessage)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(testMessage)

        verify(testDispatcher, times(3)).dispatch(testMessage, node)
    }
}