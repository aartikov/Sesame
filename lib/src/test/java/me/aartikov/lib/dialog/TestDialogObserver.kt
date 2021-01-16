package me.aartikov.lib.dialog

import me.aartikov.lib.utils.TestLifecycleOwner

class TestDialogObserver(
    override val dialogObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : DialogObserver