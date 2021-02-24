package me.aartikov.lib.activable

import androidx.lifecycle.Lifecycle
import me.aartikov.lib.activable.utils.TestLifecycleOwner
import org.junit.Test

class BindActivableToLifecycleTest {

    @Test
    fun `is inactive initially`() {
        val activable = Activable()

        assert(!activable.active)
    }

    @Test
    fun `is inactive until lifecycle is not started`() {
        val activable = Activable()
        val testLifecycleOwner = TestLifecycleOwner()
        activable.bindToLifecycle(testLifecycleOwner.lifecycle)

        testLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        assert(!activable.active)
    }

    @Test
    fun `is active when lifecycle is started`() {
        val activable = Activable()
        val testLifecycleOwner = TestLifecycleOwner()
        activable.bindToLifecycle(testLifecycleOwner.lifecycle)

        testLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        assert(activable.active)
    }

    @Test
    fun `is inactive after lifecycle is stopped`() {
        val activable = Activable()
        val testLifecycleOwner = TestLifecycleOwner()
        activable.bindToLifecycle(testLifecycleOwner.lifecycle)

        testLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        testLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        assert(!activable.active)
    }
}