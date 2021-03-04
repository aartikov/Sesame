package me.aartikov.sesame.dialog.utils

import me.aartikov.sesame.dialog.DialogObserver
import me.aartikov.sesame.utils.TestLifecycleOwner

class TestDialogObserver(
    override val dialogObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : DialogObserver