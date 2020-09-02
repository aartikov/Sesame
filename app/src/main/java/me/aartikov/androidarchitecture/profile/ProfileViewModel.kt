package me.aartikov.androidarchitecture.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.androidarchitecture.profile.domain.Profile
import me.aartikov.lib.data_binding.computed
import me.aartikov.lib.data_binding.stateFromFlow
import me.aartikov.lib.loading.simple.Loading
import me.aartikov.lib.loading.simple.handleErrors
import me.aartikov.lib.loading.simple.startIn
import me.aartikov.lib.loading.simple.uiState

class ProfileViewModel @ViewModelInject constructor(
    private val profileLoading: Loading<Profile>
) : BaseViewModel() {

    private val profileState by stateFromFlow(profileLoading.stateFlow)
    val profileUiState by computed(::profileState) { it.uiState }

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