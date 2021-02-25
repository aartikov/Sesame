package me.aartikov.sesame.property.utils


import me.aartikov.sesame.property.PropertyObserver


class TestPropertyObserver(
    override val propertyObserverLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner()
) : PropertyObserver {

}