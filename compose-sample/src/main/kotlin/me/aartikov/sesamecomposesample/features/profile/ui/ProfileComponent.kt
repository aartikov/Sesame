package me.aartikov.sesamecomposesample.features.profile.ui

import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesamecomposesample.features.profile.domain.Profile

interface ProfileComponent {

    val profileState: Loading.State<Profile>

    fun onPullToRefresh()

    fun onRetryClicked()
}