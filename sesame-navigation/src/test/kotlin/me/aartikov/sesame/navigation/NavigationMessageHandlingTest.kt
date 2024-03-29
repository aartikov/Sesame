package me.aartikov.sesame.navigation

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import me.aartikov.sesame.navigation.utils.MainDispatcherRule
import me.aartikov.sesame.navigation.utils.TestLifecycleOwner
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class NavigationMessageHandlingTest {

    companion object {
        private val TEST_MESSAGE = object : NavigationMessage {}
        private val TEST_MESSAGE_1 = object : NavigationMessage {}
        private val TEST_MESSAGE_2 = object : NavigationMessage {}
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `doesn't handle messages when lifecycleOwner is not resumed`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        val testHandler = mockNavigationMessageHandler { true }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)
        val queue = NavigationMessageQueue()
        queue.bind(dispatcher, testHandler, lifecycleOwner)

        dispatcher.resume()
        lifecycleOwner.moveToState(Lifecycle.State.STARTED)
        queue.send(TEST_MESSAGE)
        runCurrent()

        verifyNoInteractions(testHandler)
        verifyNoInteractions(testOnError)
    }

    @Test
    fun `doesn't handle messages when dispatcher is not resumed`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        val testHandler = mockNavigationMessageHandler { true }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)
        val queue = NavigationMessageQueue()
        queue.bind(dispatcher, testHandler, lifecycleOwner)

        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(TEST_MESSAGE)
        runCurrent()

        verifyNoInteractions(testHandler)
        verifyNoInteractions(testOnError)
    }

    @Test
    fun `handles messages when dispatcher and lifecycleOwner are resumed`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        val testHandler = mockNavigationMessageHandler { true }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)
        val queue = NavigationMessageQueue()
        queue.bind(dispatcher, testHandler, lifecycleOwner)

        dispatcher.resume()
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(TEST_MESSAGE)
        runCurrent()

        verify(testHandler, times(1)).handleNavigationMessage(TEST_MESSAGE)
        verifyNoInteractions(testOnError)
    }

    @Test
    fun `buffers messages when dispatcher or lifecycleOwner are not resumed`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        val testHandler = mockNavigationMessageHandler { true }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)
        val queue = NavigationMessageQueue()
        queue.bind(dispatcher, testHandler, lifecycleOwner)

        queue.send(TEST_MESSAGE)
        queue.send(TEST_MESSAGE)
        dispatcher.resume()
        queue.send(TEST_MESSAGE)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(TEST_MESSAGE)
        runCurrent()

        verify(testHandler, times(4)).handleNavigationMessage(TEST_MESSAGE)
        verifyNoInteractions(testOnError)
    }

    @Test
    fun `handles navigation messages by a chain of handlers`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        val ignoringHandler = mockNavigationMessageHandler { false }
        val workingHandler = mockNavigationMessageHandler { true }
        val unreachableHandler = mockNavigationMessageHandler { true }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(ignoringHandler, workingHandler, unreachableHandler, onError = testOnError)
        val queue = NavigationMessageQueue()
        queue.bind(dispatcher, ignoringHandler, lifecycleOwner)

        dispatcher.resume()
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(TEST_MESSAGE)
        runCurrent()

        verify(ignoringHandler).handleNavigationMessage(TEST_MESSAGE)
        verify(workingHandler).handleNavigationMessage(TEST_MESSAGE)
        verifyNoInteractions(unreachableHandler)
    }

    @Test
    fun `returns error when a message is not handled`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        val testHandler = mockNavigationMessageHandler { false }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)
        val queue = NavigationMessageQueue()
        queue.bind(dispatcher, testHandler, lifecycleOwner)

        dispatcher.resume()
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        queue.send(TEST_MESSAGE)
        runCurrent()

        verify(testOnError).invoke(NavigationError.MessageIsNotHandled(TEST_MESSAGE))
    }

    @Test
    fun `returns error when dispatcher is not resumed and dispatch is called directly`() = runTest {
        val testHandler = mockNavigationMessageHandler { true }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)

        dispatcher.dispatch(TEST_MESSAGE, testHandler)
        runCurrent()

        verify(testOnError).invoke(NavigationError.DispatcherCantHandleMessages(TEST_MESSAGE))
        verifyNoInteractions(testHandler)
    }

    @Test
    fun `doesn't overlap message handling when sending through different queues`() =
        runTest {      // Prevents "FragmentManager is already executing transactions" exception
            var log = ""
            val lifecycleOwner = TestLifecycleOwner()
            val queue1 = NavigationMessageQueue()
            val queue2 = NavigationMessageQueue()
            val testHandler = mockNavigationMessageHandler { message ->
                when (message) {
                    TEST_MESSAGE_1 -> {
                        log += "m1_begin "
                        queue2.send(TEST_MESSAGE_2)
                        log += "m1_end "
                    }
                    TEST_MESSAGE_2 -> {
                        log += "m2"
                    }
                }
                true
            }
            val testOnError = mock<Function1<NavigationError, Unit>>()
            val dispatcher = createDispatcher(testHandler, onError = testOnError)
            queue1.bind(dispatcher, testHandler, lifecycleOwner)
            queue2.bind(dispatcher, testHandler, lifecycleOwner)

            dispatcher.resume()
            lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
            queue1.send(TEST_MESSAGE_1)
            runCurrent()

            Assert.assertEquals("m1_begin m1_end m2", log)
            verifyNoInteractions(testOnError)
        }

    private fun mockNavigationMessageHandler(handle: (NavigationMessage) -> Boolean): NavigationMessageHandler {
        return mock {
            on { handleNavigationMessage(any()) } doAnswer { handle(it.getArgument(0)) }
        }
    }

    private fun createDispatcher(
        vararg handlers: Any,
        onError: ((NavigationError) -> Unit)? = null
    ): NavigationMessageDispatcher {
        val nodeWalker = object : NodeWalker {
            override fun getNextNode(node: Any): Any? {
                return handlers.getOrNull(handlers.indexOf(node) + 1)
            }
        }
        return NavigationMessageDispatcher(nodeWalker, onError)
    }
}