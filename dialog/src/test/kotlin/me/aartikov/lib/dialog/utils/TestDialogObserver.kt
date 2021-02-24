package me.aartikov.lib.dialog.utils

import me.aartikov.lib.dialog.DialogObserver
import me.aartikov.lib.utils.TestLifecycleOwner

class TestDialogObserver(
    override val dialogObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : DialogObserver