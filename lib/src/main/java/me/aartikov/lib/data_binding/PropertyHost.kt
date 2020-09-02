package me.aartikov.lib.data_binding

import kotlinx.coroutines.CoroutineScope

interface PropertyHost {
    val propertyHostScope: CoroutineScope
}