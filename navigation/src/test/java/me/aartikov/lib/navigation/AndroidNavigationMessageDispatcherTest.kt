package me.aartikov.lib.navigation

import androidx.lifecycle.Lifecycle
import com.nhaarman.mockitokotlin2.*
import me.aartikov.lib.navigation.utils.MainDispatcherRule
import me.aartikov.lib.navigation.utils.TestLifecycleOwner
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class AndroidNavigationMessageDispatcherTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `doesn't handles navigation messages when is not attached`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(testHandler)

        dispatcher.dispatch(testMessage, testHandler)

        verifyZeroInteractions(testHandler)
    }

    @Test
    fun `doesn't handles navigation messages when is not resumed`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(testHandler)
        val lifecycleOwner = TestLifecycleOwner()
        dispatcher.attach(lifecycleOwner)

        lifecycleOwner.moveToState(Lifecycle.State.STARTED)
        dispatcher.dispatch(testMessage, testHandler)

        verifyZeroInteractions(testHandler)
    }

    @Test
    fun `handles navigation messages when is resumed`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(testHandler)
        val lifecycleOwner = TestLifecycleOwner()
        dispatcher.attach(lifecycleOwner)

        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        dispatcher.dispatch(testMessage, testHandler)

        verify(testHandler).handleNavigationMessage(testMessage)
    }

    @Test
    fun `buffers navigation messages`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(testHandler)
        val lifecycleOwner = TestLifecycleOwner()
        dispatcher.attach(lifecycleOwner)

        lifecycleOwner.moveToState(Lifecycle.State.STARTED)
        dispatcher.dispatch(testMessage, testHandler)
        dispatcher.dispatch(testMessage, testHandler)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        dispatcher.dispatch(testMessage, testHandler)

        verify(testHandler, times(3)).handleNavigationMessage(testMessage)
    }


    @Test
    fun `handles navigation messages by a chain of handlers`() {
        val testMessage = object : NavigationMessage {}
        val ignoringHandler = mockNavigationMessageHandler { false }
        val workingHandler = mockNavigationMessageHandler { true }
        val unreachableHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(ignoringHandler, workingHandler, unreachableHandler)
        val lifecycleOwner = TestLifecycleOwner()
        dispatcher.attach(lifecycleOwner)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)

        dispatcher.dispatch(testMessage, ignoringHandler)

        verify(ignoringHandler).handleNavigationMessage(testMessage)
        verify(workingHandler).handleNavigationMessage(testMessage)
        verifyZeroInteractions(unreachableHandler)
    }

    @Test
    fun `fails when message is not handled`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { false }
        val exceptions = mutableListOf<Throwable>()
        val dispatcher = createDispatcher(testHandler) { exception -> exceptions.add(exception) }
        val lifecycleOwner = TestLifecycleOwner()
        dispatcher.attach(lifecycleOwner)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)

        dispatcher.dispatch(testMessage, testHandler)

        assertEquals(1, exceptions.size)
        assert(exceptions[0] is NotHandledNavigationMessageException)
    }

    @Test
    fun `doesn't overlap message handling`() {      // Prevents "FragmentManager is already executing transactions" exception
        val testMessage1 = object : NavigationMessage {}
        val testMessage2 = object : NavigationMessage {}

        lateinit var dispatcher: NavigationMessageDispatcher
        var log = ""

        val testHandler1 = mockNavigationMessageHandler { message ->
            if (message == testMessage1) {
                log += "m1_begin "
                dispatcher.dispatch(testMessage2, this)
                log += "m1_end "
                true
            } else {
                false
            }
        }

        val testHandler2 = mockNavigationMessageHandler { message ->
            if (message == testMessage2) {
                log += "m2"
                true
            } else {
                false
            }
        }

        dispatcher = createDispatcher(testHandler1, testHandler2)
        val lifecycleOwner = TestLifecycleOwner()
        dispatcher.attach(lifecycleOwner)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)

        dispatcher.dispatch(testMessage1, testHandler1)

        assertEquals("m1_begin m1_end m2", log)
    }

    @Test
    fun `drops unhandled messages after reattach`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(testHandler)
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()

        dispatcher.attach(lifecycleOwner1)
        lifecycleOwner1.moveToState(Lifecycle.State.STARTED)
        dispatcher.dispatch(testMessage, testHandler)
        dispatcher.dispatch(testMessage, testHandler)

        lifecycleOwner2.moveToState(Lifecycle.State.STARTED)
        dispatcher.attach(lifecycleOwner2)
        dispatcher.dispatch(testMessage, testHandler)
        lifecycleOwner2.moveToState(Lifecycle.State.RESUMED)

        verify(testHandler, times(1)).handleNavigationMessage(testMessage)
    }

    private fun mockNavigationMessageHandler(handle: (NavigationMessage) -> Boolean): NavigationMessageHandler {
        return mock {
            on { handleNavigationMessage(any()) } doAnswer { handle(it.getArgument(0)) }
        }
    }

    private fun createDispatcher(
        vararg handlers: Any,
        errorHandler: ((Exception) -> Unit)? = null
    ): NavigationMessageDispatcher {
        val nodeWalker = object : NodeWalker {
            override fun getNextNode(node: Any): Any? {
                return handlers.getOrNull(handlers.indexOf(node) + 1)
            }
        }
        return AndroidNavigationMessageDispatcher(nodeWalker, errorHandler)
    }
}