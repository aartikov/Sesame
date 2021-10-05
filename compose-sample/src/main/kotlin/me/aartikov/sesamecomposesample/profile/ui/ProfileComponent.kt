package me.aartikov.sesamecomposesample.profile.ui


import kotlinx.coroutines.flow.StateFlow
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesamecomposesample.profile.domain.Profile

interface ProfileComponent {
    val profileState: StateFlow<Loading.State<Profile>>

    fun onPullToRefresh()

    fun onRetryClicked()
}