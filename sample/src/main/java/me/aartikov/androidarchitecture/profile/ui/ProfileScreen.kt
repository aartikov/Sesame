package me.aartikov.androidarchitecture.profile.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_error_view.*
import kotlinx.android.synthetic.main.screen_profile.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.lib.core.loading.simple.Loading

@AndroidEntryPoint
class ProfileScreen : BaseScreen<ProfileViewModel>(R.layout.screen_profile, ProfileViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }
        retryButton.setOnClickListener { vm.onRetryClicked() }

        vm::profileState bind { state ->
            swipeRefresh.isVisible = state is Loading.State.Data
            errorView.isVisible = state is Loading.State.Error
            loadingView.isVisible = state is Loading.State.Loading

            when (state) {
                is Loading.State.Data -> {
                    swipeRefresh.isRefreshing = state.refreshing
                    val profile = state.data
                    name.text = profile.name
                    Glide.with(avatar)
                        .load(profile.avatarUrl)
                        .placeholder(R.drawable.bg_avatar)
                        .circleCrop()
                        .into(avatar)
                }

                is Loading.State.Error -> {
                    errorMessage.text = state.throwable.message
                }
            }
        }
    }
}