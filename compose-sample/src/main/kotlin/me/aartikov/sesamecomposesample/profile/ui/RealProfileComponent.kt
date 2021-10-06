package me.aartikov.sesamecomposesample.profile.ui

import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesamecomposesample.utils.componentCoroutineScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class RealProfileComponent(
    componentContext: ComponentContext,
    viewModelStoreOwner: ViewModelStoreOwner
) : ComponentContext by componentContext, ProfileComponent {

    private val coroutineScope = componentCoroutineScope()

    private val profileViewModel: ProfileViewModel by viewModelStoreOwner.viewModel()

    private val profileLoading = OrdinaryLoading(
        coroutineScope,
        load = { profileViewModel.loadProfile() }
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