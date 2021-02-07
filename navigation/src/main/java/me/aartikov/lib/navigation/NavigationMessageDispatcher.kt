package me.aartikov.lib.navigation

import androidx.lifecycle.LifecycleOwner

interface NavigationMessageDispatcher {

    fun attach(lifecycleOwner: LifecycleOwner)

    fun dispatch(message: NavigationMessage, firstNode: Any)
}