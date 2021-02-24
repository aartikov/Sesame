package me.aartikov.androidarchitecture.profile.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.databinding.ScreenProfileBinding
import me.aartikov.lib.loading.simple.Loading

@AndroidEntryPoint
class ProfileScreen : BaseScreen<ProfileViewModel>(R.layout.screen_profile, ProfileViewModel::class) {

    private val binding by viewBinding(ScreenProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }
            errorView.retryButton.setOnClickListener { vm.onRetryClicked() }

            vm::profileState bind { state ->
                swipeRefresh.isVisible = state is Loading.State.Data
                errorView.root.isVisible = state is Loading.State.Error
                loadingView.root.isVisible = state is Loading.State.Loading

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
                        errorView.errorMessage.text = state.throwable.message
                    }
                }
            }
        }
    }
}