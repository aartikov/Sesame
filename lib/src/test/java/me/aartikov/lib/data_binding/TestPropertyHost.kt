package me.aartikov.lib.data_binding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScope

open class TestPropertyHost(
    override val propertyHostScope: CoroutineScope = TestCoroutineScope()
) : PropertyHost