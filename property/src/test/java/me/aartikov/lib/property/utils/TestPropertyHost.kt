package me.aartikov.lib.property.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScope
import me.aartikov.lib.property.PropertyHost

open class TestPropertyHost(
    override val propertyHostScope: CoroutineScope = TestCoroutineScope()
) : PropertyHost