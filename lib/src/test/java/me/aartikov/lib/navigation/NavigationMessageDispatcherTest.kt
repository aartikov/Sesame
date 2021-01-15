package me.aartikov.lib.navigation

import com.nhaarman.mockitokotlin2.*
import me.aartikov.lib.utils.DispatchersTestRule
import me.aartikov.lib.utils.TestLifecycleOwner
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class NavigationMessageDispatcherTest {

    @get:Rule
    val dispatchersTestRule = DispatchersTestRule()

    @Test
    fun `doesn't handles navigation messages when is not attached`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val testDispatcher = createDispatcher(testHandler)

        testDispatcher.dispatch(testMessage, testHandler)

        verifyZeroInteractions(testHandler)
    }

    @Test
    fun `doesn't handles navigation messages when is not resumed`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val testDispatcher = createDispatcher(testHandler)
        val lifecycleOwner = TestLifecycleOwner()
        testDispatcher.attach(lifecycleOwner)

        lifecycleOwner.onCreate()
        lifecycleOwner.onStart()
        testDispatcher.dispatch(testMessage, testHandler)

        verifyZeroInteractions(testHandler)
    }

    @Test
    fun `handles navigation messages when is resumed`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val testDispatcher = createDispatcher(testHandler)
        val lifecycleOwner = TestLifecycleOwner()
        testDispatcher.attach(lifecycleOwner)

        lifecycleOwner.onCreate()
        lifecycleOwner.onStart()
        lifecycleOwner.onResume()
        testDispatcher.dispatch(testMessage, testHandler)

        verify(testHandler).handleNavigationMessage(testMessage)
    }

    @Test
    fun `buffers navigation messages`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { true }
        val testDispatcher = createDispatcher(testHandler)
        val lifecycleOwner = TestLifecycleOwner()
        testDispatcher.attach(lifecycleOwner)

        lifecycleOwner.onCreate()
        lifecycleOwner.onStart()
        testDispatcher.dispatch(testMessage, testHandler)
        testDispatcher.dispatch(testMessage, testHandler)
        lifecycleOwner.onResume()
        testDispatcher.dispatch(testMessage, testHandler)

        verify(testHandler, times(3)).handleNavigationMessage(testMessage)
    }


    @Test
    fun `handles navigation messages by a chain of handlers`() {
        val testMessage = object : NavigationMessage {}
        val ignoringHandler = mockNavigationMessageHandler { false }
        val workingHandler = mockNavigationMessageHandler { true }
        val unreachableHandler = mockNavigationMessageHandler { true }
        val testDispatcher = createDispatcher(ignoringHandler, workingHandler, unreachableHandler)
        val lifecycleOwner = TestLifecycleOwner()
        testDispatcher.attach(lifecycleOwner)

        lifecycleOwner.onCreate()
        lifecycleOwner.onStart()
        lifecycleOwner.onResume()
        testDispatcher.dispatch(testMessage, ignoringHandler)

        verify(ignoringHandler).handleNavigationMessage(testMessage)
        verify(workingHandler).handleNavigationMessage(testMessage)
        verifyZeroInteractions(unreachableHandler)
    }

    @Test
    fun `fails when message is not handled`() {
        val testMessage = object : NavigationMessage {}
        val testHandler = mockNavigationMessageHandler { false }
        val exceptions = mutableListOf<Throwable>()
        val testDispatcher = createDispatcher(testHandler) { exception -> exceptions.add(exception) }
        val lifecycleOwner = TestLifecycleOwner()
        testDispatcher.attach(lifecycleOwner)

        lifecycleOwner.onCreate()
        lifecycleOwner.onStart()
        lifecycleOwner.onResume()

        testDispatcher.dispatch(testMessage, testHandler)

        assertEquals(1, exceptions.size)
        assert(exceptions[0] is NotHandledNavigationMessageException)
    }

    @Test
    fun `doesn't overlap message handling`() {      // Prevents "FragmentManager is already executing transactions" exception
        val testMessage1 = object : NavigationMessage {}
        val testMessage2 = object : NavigationMessage {}

        lateinit var testDispatcher: NavigationMessageDispatcher
        var log = ""

        val testHandler1 = mockNavigationMessageHandler { message ->
            if (message == testMessage1) {
                log += "m1_begin "
                testDispatcher.dispatch(testMessage2, this)
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

        testDispatcher = createDispatcher(testHandler1, testHandler2)
        val lifecycleOwner = TestLifecycleOwner()
        testDispatcher.attach(lifecycleOwner)
        lifecycleOwner.onCreate()
        lifecycleOwner.onStart()
        lifecycleOwner.onResume()

        testDispatcher.dispatch(testMessage1, testHandler1)

        assertEquals("m1_begin m1_end m2", log)
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
        return object : NavigationMessageDispatcher(errorHandler) {
            override fun getParentNode(node: Any): Any? {
                return handlers.getOrNull(handlers.indexOf(node) + 1)
            }
        }
    }
}