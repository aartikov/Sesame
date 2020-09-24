package me.aartikov.lib.data_binding

import androidx.lifecycle.LifecycleOwner

class TestPropertyObserver(
    override val propertyObserverLifecycleOwner: LifecycleOwner
) : PropertyObserver