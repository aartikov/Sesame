package me.aartikov.sesamesample.profile.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentProfileBinding

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>(R.layout.fragment_profile, ProfileViewModel::class) {

    override val titleRes: Int = R.string.profile_title

    private val binding by viewBinding(FragmentProfileBinding::bind)

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