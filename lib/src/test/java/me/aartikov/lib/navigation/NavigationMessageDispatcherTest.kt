package me.aartikov.lib.navigation

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import kotlin.test.assertFailsWith

class NavigationMessageDispatcherTest {

    @Test
    fun `handles navigation messages`() {
        val testMessage = object : NavigationMessage {}
        val workingHandler = mockNavigationMessageHandler(true)
        val ignoringHandler = mockNavigationMessageHandler(false)
        val unreachableHandler = mockNavigationMessageHandler(true)
        val testDispatcher = createDispatcher(listOf(Unit, ignoringHandler, workingHandler, unreachableHandler))

        testDispatcher.dispatch(testMessage)

        verify(ignoringHandler).handleNavigationMessage(testMessage)
        verify(workingHandler).handleNavigationMessage(testMessage)
        verifyZeroInteractions(unreachableHandler)
    }

    @Test
    fun `fails when message is not handled`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler(false)
        val testDispatcher = createDispatcher(listOf(Unit, testHandler))

        assertFailsWith<NotHandledNavigationMessageException> {
            testDispatcher.dispatch(testMessage)
        }
    }

    private fun mockNavigationMessageHandler(handleMessages: Boolean): NavigationMessageHandler {
        return mock {
            on { handleNavigationMessage(any())} doReturn handleMessages
        }
    }

    private fun createDispatcher(handlers: List<Any>): NavigationMessageDispatcher {
        return object : NavigationMessageDispatcher(Unit) {

            var k = 0

            override fun getParent(node: Any?): Any? {

                val result: Any? = try {
                    handlers[k]
                } catch (e: ArrayIndexOutOfBoundsException) {
                    null
                }
                k++

                return result
            }
        }
    }
}