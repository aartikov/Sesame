package me.aartikov.lib.widget

import me.aartikov.lib.utils.TestLifecycleOwner

class TestWidgetObserver(
    override val widgetObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : WidgetObserver