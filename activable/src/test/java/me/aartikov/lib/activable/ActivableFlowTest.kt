package me.aartikov.lib.activable

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ActivableFlowTest {

    @Test
    fun `emits nothing when inactive`() = runBlockingTest {
        val scope = this
        val activable = Activable()
        val originalFlow = MutableSharedFlow<Int>()
        val activableFlow = activableFlow(originalFlow, activable, scope)
        val items = mutableListOf<Int>()

        activableFlow
            .onEach { items.add(it) }
            .launchIn(scope)

        originalFlow.emit(1)
        originalFlow.emit(2)
        originalFlow.emit(3)

        assertEquals(emptyList<Int>(), items)
        scope.cancelChildren()
    }

    @Test
    fun `emits when active`() = runBlockingTest {
        val scope = this
        val activable = Activable()
        val originalFlow = MutableSharedFlow<Int>()
        val activableFlow = activableFlow(originalFlow, activable, scope)
        val items = mutableListOf<Int>()

        activableFlow
            .onEach { items.add(it) }
            .launchIn(scope)


        originalFlow.emit(1)
        activable.onActive()
        originalFlow.emit(2)
        originalFlow.emit(3)
        activable.onInactive()
        originalFlow.emit(4)

        assertEquals(listOf(2, 3), items)
        scope.cancelChildren()
    }

    private fun CoroutineScope.cancelChildren() {
        val job = coroutineContext[Job]!!
        job.children.drop(1)    // drop DeferredCoroutine from internals of runBlockingTest
            .forEach { it.cancel() }
    }
}