package me.aartikov.sesamecomposesample.profile.ui

import androidx.compose.runtime.*
import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesamecomposesample.profile.data.ProfileGateway
import me.aartikov.sesamecomposesample.utils.componentCoroutineScope
import me.aartikov.sesamecomposesample.utils.toComposeState

class RealProfileComponent(
    componentContext: ComponentContext,
    private val profileGateway: ProfileGateway
) : ComponentContext by componentContext, ProfileComponent {

    private val coroutineScope = componentCoroutineScope()

    private val profileLoading = OrdinaryLoading(
        coroutineScope,
        load = { profileGateway.loadProfile() }
    )

    override val profileState by profileLoading.stateFlow.toComposeState(
        coroutineScope,
        Loading.State.Loading
    )

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