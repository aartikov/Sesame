package me.aartikov.sesamecomposesample.features.profile

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.features.profile.data.ProfileGateway
import me.aartikov.sesamecomposesample.features.profile.data.ProfileGatewayImpl
import me.aartikov.sesamecomposesample.features.profile.ui.ProfileComponent
import me.aartikov.sesamecomposesample.features.profile.ui.RealProfileComponent
import org.koin.core.component.get
import org.koin.dsl.module

val profileModule = module {
    single<ProfileGateway> { ProfileGatewayImpl() }
}

fun ComponentFactory.createProfileComponent(componentContext: ComponentContext): ProfileComponent {
    return RealProfileComponent(componentContext, get(), get())
}