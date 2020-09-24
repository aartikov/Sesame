package me.aartikov.lib.data_binding

import me.aartikov.lib.utils.TestLifecycleOwner

class TestPropertyObserver(
    override val propertyObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : PropertyObserver {

}