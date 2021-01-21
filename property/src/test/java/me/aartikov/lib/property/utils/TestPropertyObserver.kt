package me.aartikov.lib.property.utils


import me.aartikov.lib.property.PropertyObserver


class TestPropertyObserver(
    override val propertyObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : PropertyObserver {

}