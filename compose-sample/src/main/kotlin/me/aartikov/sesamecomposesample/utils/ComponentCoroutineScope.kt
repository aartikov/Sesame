package me.aartikov.sesamecomposesample.utils

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

fun LifecycleOwner.componentCoroutineScope(): CoroutineScope {
    val scope = MainScope()
    lifecycle.doOnDestroy {
        scope.cancel()
    }
    return scope
}