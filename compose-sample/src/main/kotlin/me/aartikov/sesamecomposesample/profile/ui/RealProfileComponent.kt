package me.aartikov.sesamecomposesample.profile.ui

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesamecomposesample.utils.componentCoroutineScope
import me.aartikov.sesamecomposesample.profile.data.ProfileGateway

class RealProfileComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, ProfileComponent {

    private val coroutineScope = componentCoroutineScope()

    private val profileGateway: ProfileGateway = ProfileGateway()

    private val profileLoading = OrdinaryLoading(
        coroutineScope,
        load = { profileGateway.loadProfile() }
    )

    override val profileState = profileLoading.stateFlow

    init {
        profileLoading.handleErrors(coroutineScope) { error ->
            if (error.hasData) {
                // TODO: Show toast
            }
        }

        profileLoading.refresh()
    }

    override fun onPullToRefresh() {
        profileLoading.refresh()
    }

    override fun onRetryClicked() {
        profileLoading.refresh()
    }
}