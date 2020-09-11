package me.aartikov.lib.widget

import kotlinx.coroutines.CoroutineScope

interface WidgetHost {
    val widgetHostScope: CoroutineScope
}