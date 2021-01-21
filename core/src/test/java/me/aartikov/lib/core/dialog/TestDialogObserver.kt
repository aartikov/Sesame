package me.aartikov.lib.core.dialog

import me.aartikov.lib.utils.TestLifecycleOwner

class TestDialogObserver(
    override val dialogObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : DialogObserver