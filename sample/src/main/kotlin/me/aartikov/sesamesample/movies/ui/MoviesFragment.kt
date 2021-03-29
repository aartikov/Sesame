package me.aartikov.sesamesample.movies.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentMoviesBinding
import me.aartikov.sesamesample.movies.utils.doOnScrollToEnd


@AndroidEntryPoint
class MoviesFragment : BaseFragment<MoviesViewModel>(R.layout.fragment_movies, MoviesViewModel::class) {

    override val titleRes: Int = R.string.movies_title

    private val binding by viewBinding(FragmentMoviesBinding::bind)

    private val movieAdapter = GroupieAdapter()
    private val listSection = Section()
    private var loadMoreEnabled: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            initRecyclerView()
            swipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }
            emptyView.retryButton.setOnClickListener { vm.onRetryClicked() }
            errorView.retryButton.setOnClickListener { vm.onRetryClicked() }

            vm::moviesState bind { state ->
                swipeRefresh.isVisible = state is PagedLoading.State.Data
                emptyView.root.isVisible = state is PagedLoading.State.Empty
                loadingView.root.isVisible = state is PagedLoading.State.Loading
                errorView.root.isVisible = state is PagedLoading.State.Error

                when (state) {
                    is PagedLoading.State.Data -> {
                        listSection.update(state.data.toGroupieItems())
                        swipeRefresh.isRefreshing = state.refreshing

                        if (state.loadingMore) {
                            listSection.setFooter(LoadingItem())
                        } else {
                            listSection.removeFooter()
                        }
                    }

                    is PagedLoading.State.Error -> {
                        errorView.errorMessage.text = state.throwable.message
                    }
                }

                loadMoreEnabled = state is PagedLoading.State.Data && state.loadMoreEnabled
            }
        }
    }

    private fun initRecyclerView() {
        movieAdapter.add(listSection)
        with(binding.moviesList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            doOnScrollToEnd {
                if (loadMoreEnabled) vm.onLoadMore()
            }
        }
    }
}