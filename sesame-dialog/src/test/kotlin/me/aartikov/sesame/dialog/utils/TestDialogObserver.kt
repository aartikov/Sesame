package me.aartikov.sesame.dialog.utils

import me.aartikov.sesame.dialog.DialogObserver

class TestDialogObserver(
    override val dialogObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : DialogObserver