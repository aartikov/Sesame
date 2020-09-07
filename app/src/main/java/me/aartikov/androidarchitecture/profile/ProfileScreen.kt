package me.aartikov.androidarchitecture.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_profile.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.lib.loading.simple.setToView

@AndroidEntryPoint
class ProfileScreen : BaseScreen<ProfileViewModel>(R.layout.screen_profile, ProfileViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }
        retryButton.setOnClickListener { vm.onRetryClicked() }
        logoutButton.setOnClickListener { vm.onLogoutClicked() }

        vm::profileUiState bind { state ->
            state.setToView(
                setData = { profile ->
                    name.text = profile.name
                    Glide.with(avatar)
                        .load(profile.avatarUrl)
                        .placeholder(R.drawable.bg_avatar)
                        .circleCrop()
                        .into(avatar)
                },
                setDataVisible = swipeRefresh::isVisible::set,
                setError = {
                    errorMessage.text = it.message
                },
                setErrorVisible = errorView::isVisible::set,
                setLoadingVisible = loadingView::isVisible::set,
                setRefreshVisible = swipeRefresh::setRefreshing,
                setRefreshEnabled = swipeRefresh::setEnabled
            )
        }
    }
}