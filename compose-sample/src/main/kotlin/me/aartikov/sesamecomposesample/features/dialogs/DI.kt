package me.aartikov.sesamecomposesample.features.dialogs

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.features.dialogs.ui.DialogsComponent
import me.aartikov.sesamecomposesample.features.dialogs.ui.RealDialogsComponent
import org.koin.core.component.get

fun ComponentFactory.createDialogsComponent(componentContext: ComponentContext): DialogsComponent {
    return RealDialogsComponent(componentContext, get())
}