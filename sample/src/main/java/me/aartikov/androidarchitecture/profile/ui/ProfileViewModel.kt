package me.aartikov.androidarchitecture.profile.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.androidarchitecture.profile.data.ProfileGateway
import me.aartikov.lib.loading.simple.OrdinaryLoading
import me.aartikov.lib.loading.simple.handleErrors
import me.aartikov.lib.loading.simple.refresh
import me.aartikov.lib.property.stateFromFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
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
        profileLoading.attach(viewModelScope)
        profileLoading.refresh()
    }

    fun onPullToRefresh() {
        profileLoading.refresh()
    }

    fun onRetryClicked() {
        profileLoading.refresh()
    }
}