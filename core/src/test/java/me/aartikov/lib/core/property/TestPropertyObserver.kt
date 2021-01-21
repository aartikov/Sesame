package me.aartikov.lib.core.property

import me.aartikov.lib.utils.TestLifecycleOwner

class TestPropertyObserver(
    override val propertyObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : PropertyObserver {

}