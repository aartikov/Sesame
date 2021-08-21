package me.aartikov.sesamesample.profile.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.stateFromFlow
import me.aartikov.sesamesample.base.BaseViewModel
import me.aartikov.sesamesample.profile.data.ProfileGateway
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileGateway: ProfileGateway
) : BaseViewModel() {

    private val profileLoading = OrdinaryLoading(
        viewModelScope,
        load = { profileGateway.loadProfile() }
    )

    val profileState by stateFromFlow(profileLoading.stateFlow)

    init {
        profileLoading.handleErrors(viewModelScope) { error ->
            if (error.hasData) {
                showError(error.throwable)
            }
        }

        profileLoading.refresh()
    }

    fun onPullToRefresh() {
        profileLoading.refresh()
    }

    fun onRetryClicked() {
        profileLoading.refresh()
    }
}