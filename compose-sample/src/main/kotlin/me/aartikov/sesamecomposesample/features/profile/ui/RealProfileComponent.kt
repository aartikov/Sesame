package me.aartikov.sesamecomposesample.features.profile.ui

import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.core.message.MessageService
import me.aartikov.sesamecomposesample.core.utils.componentCoroutineScope
import me.aartikov.sesamecomposesample.core.utils.toComposeState
import me.aartikov.sesamecomposesample.features.profile.data.ProfileGateway

class RealProfileComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService,
    private val profileGateway: ProfileGateway
) : ComponentContext by componentContext, ProfileComponent {

    private val coroutineScope = componentCoroutineScope()

    private val profileLoading = OrdinaryLoading(
        coroutineScope,
        load = { profileGateway.loadProfile() }
    )

    override val profileState by profileLoading.stateFlow.toComposeState(coroutineScope)

    init {
        profileLoading.handleErrors(coroutineScope) { error ->
            if (error.hasData) {
                val message = error.throwable.message
                messageService.showMessage(LocalizedString.raw(message ?: "Error"))
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