package me.aartikov.sesamecomposesample.profile.ui

import androidx.lifecycle.ViewModel
import me.aartikov.sesamecomposesample.profile.data.ProfileGateway

class ProfileViewModel(
    private val profileGateway: ProfileGateway
) : ViewModel() {

    suspend fun loadProfile() = profileGateway.loadProfile()
}