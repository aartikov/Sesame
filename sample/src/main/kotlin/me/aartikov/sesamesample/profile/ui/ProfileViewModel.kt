package me.aartikov.sesamesample.profile.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.stateFromFlow
import kotlinx.coroutines.launch
import me.aartikov.sesamesample.base.BaseViewModel
import me.aartikov.sesamesample.profile.data.ProfileGateway
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileGateway: ProfileGateway
) : BaseViewModel() {

    private val profileLoading = OrdinaryLoading(viewModelScope, profileGateway::loadProfile)

    val profileState by stateFromFlow(profileLoading.stateFlow)

    init {
        profileLoading.handleErrors(viewModelScope) { error ->
            if (error.hasData) {
                showError(error.throwable)
            }
        }

        profileLoading.load(fresh = false)
    }

    fun onPullToRefresh() {
        profileLoading.load(fresh = true)
    }

    fun onRetryClicked() {
        profileLoading.load(fresh = true)
    }

    fun onAvatarClicked() {
        viewModelScope.launch {
            profileGateway.clearCache()
        }
    }
}