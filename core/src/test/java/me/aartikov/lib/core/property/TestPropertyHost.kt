package me.aartikov.lib.core.property

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScope

open class TestPropertyHost(
    override val propertyHostScope: CoroutineScope = TestCoroutineScope()
) : PropertyHost