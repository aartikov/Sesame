package me.aartikov.sesame.navigation

import com.nhaarman.mockitokotlin2.*
import me.aartikov.sesame.navigation.utils.MainDispatcherRule
import org.junit.Rule
import org.junit.Test

class NavigationMessageDispatcherTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `returns error when is not resumed`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)

        dispatcher.dispatch(testMessage, testHandler)

        verify(testOnError).invoke(NavigationError.DispatcherCantHandleMessages(testMessage))
        verifyZeroInteractions(testHandler)
    }

    @Test
    fun `handles navigation messages when is resumed`() {
        val testMessage = object : NavigationMessage {}
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val testHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(testOnError, testHandler)

        dispatcher.resume()
        dispatcher.dispatch(testMessage, testHandler)

        verifyZeroInteractions(testOnError)
        verify(testHandler).handleNavigationMessage(testMessage)
    }

    @Test
    fun `handles navigation messages by a chain of handlers`() {
        val testMessage = object : NavigationMessage {}
        val ignoringHandler = mockNavigationMessageHandler { false }
        val workingHandler = mockNavigationMessageHandler { true }
        val unreachableHandler = mockNavigationMessageHandler { true }
        val dispatcher = createDispatcher(ignoringHandler, workingHandler, unreachableHandler)

        dispatcher.resume()
        dispatcher.dispatch(testMessage, ignoringHandler)

        verify(ignoringHandler).handleNavigationMessage(testMessage)
        verify(workingHandler).handleNavigationMessage(testMessage)
        verifyZeroInteractions(unreachableHandler)
    }

    @Test
    fun `returns error when other message handling is in progress`() {
        val testMessage = object : NavigationMessage {}

        lateinit var dispatcher: NavigationMessageDispatcher

        val testHandler = mockNavigationMessageHandler { message ->
            dispatcher.dispatch(message, this)
            true
        }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        dispatcher = createDispatcher(testHandler, onError = testOnError)

        dispatcher.resume()
        dispatcher.dispatch(testMessage, testHandler)

        verify(testOnError).invoke(NavigationError.DispatcherCantHandleMessages(testMessage))
        verify(testHandler, times(1)).handleNavigationMessage(testMessage)
    }

    @Test
    fun `returns error when message handler missing`() {
        val testMessage = object : NavigationMessage {}

        val testHandler = mockNavigationMessageHandler { message ->
            false
        }
        val testOnError = mock<Function1<NavigationError, Unit>>()
        val dispatcher = createDispatcher(testHandler, onError = testOnError)

        dispatcher.resume()
        dispatcher.dispatch(testMessage, testHandler)

        verify(testOnError).invoke(NavigationError.MessageHandlerMissing(testMessage))
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