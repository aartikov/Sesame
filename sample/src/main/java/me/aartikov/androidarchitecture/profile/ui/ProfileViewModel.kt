package me.aartikov.androidarchitecture.profile.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.androidarchitecture.profile.data.ProfileGateway
import me.aartikov.lib.core.loading.simple.OrdinaryLoading
import me.aartikov.lib.core.loading.simple.handleErrors
import me.aartikov.lib.core.loading.simple.startIn
import me.aartikov.lib.property.stateFromFlow

class ProfileViewModel @ViewModelInject constructor(
    private val profileGateway: ProfileGateway
) : BaseViewModel() {

    private val profileLoading = OrdinaryLoading(profileGateway::loadProfile)

    val profileState by stateFromFlow(profileLoading.stateFlow)

    init {
        profileLoading.handleErrors(viewModelScope) { error ->
            if (error.hasData) {
                showError(error.throwable)
            }
        }
        profileLoading.startIn(viewModelScope)
    }

    fun onPullToRefresh() {
        profileLoading.refresh()
    }

    fun onRetryClicked() {
        profileLoading.refresh()
    }
}