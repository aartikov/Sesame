package me.aartikov.sesamecomposesample.features.form

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.features.form.ui.FormComponent
import me.aartikov.sesamecomposesample.features.form.ui.RealFormComponent


fun ComponentFactory.createFormComponent(componentContext: ComponentContext): FormComponent {
    return RealFormComponent(componentContext)
}