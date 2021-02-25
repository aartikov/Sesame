package me.aartikov.sesame.loop

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.sesame.loop.TestAction.*
import me.aartikov.sesame.loop.TestEffect.Effect1
import me.aartikov.sesame.loop.TestEffect.Effect2
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class LoopTest {

    @Test
    fun `dispatches actions to state changes`() = runBlockingTest {
        val loop = TestLoop()

        val job = launch {
            loop.start()
        }
        loop.dispatch(Action1)
        loop.dispatch(Action2)
        job.cancel()

        assertEquals(TestState("Action1 Action2"), loop.state)
    }

    @Test
    fun `dispatches actions from external source to state changes`() = runBlockingTest {
        val actionSource = TestActionSource(Action1, Action2)
        val loop = TestLoop(actionSources = listOf(actionSource))

        val job = launch {
            loop.start()
        }
        job.cancel()

        assertEquals(TestState("Action1 Action2"), loop.state)
    }

    @Test
    fun `dispatches actions from effect handlers to state changes`() = runBlockingTest {
        val effectHandler = TestEffectHandler()
        val loop = TestLoop(effectHandlers = listOf(effectHandler))

        val job = launch {
            loop.start()
        }
        loop.dispatch(Action1)
        loop.dispatch(Action2)
        job.cancel()

        assertEquals(TestState("Action1 ActionAfterEffect1 Action2 ActionAfterEffect2"), loop.state)
    }

    @Test
    fun `fails if started twice`() {
        val loop = TestLoop()

        Assert.assertThrows(IllegalStateException::class.java) {
            runBlockingTest {
                launch {
                    loop.start()
                }
                launch {
                    loop.start()
                }
            }
        }
    }
}


private data class TestState(val value: String)

private enum class TestAction {
    Action1, Action2, ActionAfterEffect1, ActionAfterEffect2
}

private enum class TestEffect {
    Effect1, Effect2
}

private class TestLoop(
    actionSources: List<ActionSource<TestAction>> = emptyList(),
    effectHandlers: List<EffectHandler<TestEffect, TestAction>> = emptyList()
) : Loop<TestState, TestAction, TestEffect>(
    TestState(""),
    TestReducer(),
    actionSources,
    effectHandlers
)


/**
 * Creates state as join of action names
 * Creates effects: Effect1 for Action1, Effect2 for Action2
 */
private class TestReducer : Reducer<TestState, TestAction, TestEffect> {

    override fun reduce(state: TestState, action: TestAction): Next<TestState, TestEffect> {
        val effect = when (action) {
            Action1 -> Effect1
            Action2 -> Effect2
            else -> null
        }
        val newState = when (state.value) {
            "" -> TestState(action.name)
            else -> TestState(state.value + " " + action.name)
        }
        return Next(newState, listOfNotNull(effect))
    }
}

/**
 * Emits given actions
 */
private class TestActionSource(private vararg val actions: TestAction) : ActionSource<TestAction> {

    override suspend fun start(actionConsumer: (TestAction) -> Unit) {
        actions.forEach {
            actionConsumer(it)
        }
    }
}

/**
 * emits ActionAfterEffect1 for Effect1, and ActionAfterEffect2 for Effect2
 */
private class TestEffectHandler : EffectHandler<TestEffect, TestAction> {

    override suspend fun handleEffect(effect: TestEffect, actionConsumer: (TestAction) -> Unit) {
        when (effect) {
            Effect1 -> actionConsumer(ActionAfterEffect1)
            Effect2 -> actionConsumer(ActionAfterEffect2)
        }
    }
}