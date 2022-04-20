package me.aartikov.sesamecomposesample.features.movies.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.core.theme.AppTheme
import me.aartikov.sesamecomposesample.core.widgets.PlaceholderWidget
import me.aartikov.sesamecomposesample.core.widgets.ProgressWidget
import me.aartikov.sesamecomposesample.features.movies.domain.Movie

@Composable
fun MoviesUi(
    component: MoviesComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        when (val currentState = component.moviesState) {
            is PagedLoading.State.Data -> {
                MoviesContent(
                    movies = currentState.data,
                    isRefreshing = currentState.refreshing,
                    loadMoreEnabled = currentState.loadMoreEnabled,
                    loadingMore = currentState.loadingMore,
                    onRefresh = component::onPullToRefresh,
                    onLoadMore = component::onLoadMore
                )
            }

            is PagedLoading.State.Error -> {
                val message = currentState.throwable.message
                PlaceholderWidget(
                    message = message ?: stringResource(R.string.common_some_error_description),
                    onRetry = component::onRetryClicked
                )
            }

            is PagedLoading.State.Loading -> {
                ProgressWidget()
            }

            is PagedLoading.State.Empty -> {
                PlaceholderWidget(
                    message = stringResource(R.string.empty_view_text),
                    onRetry = component::onRetryClicked
                )
            }
        }
    }
}

@Composable
fun MoviesContent(
    movies: List<Movie>,
    isRefreshing: Boolean,
    loadMoreEnabled: Boolean,
    loadingMore: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        LazyColumn(state = listState) {
            items(movies) {
                ItemMovie(movie = it)
                Divider(color = MaterialTheme.colors.surface, thickness = 1.dp)
            }

            if (loadingMore) {
                item {
                    ItemLoading()
                }
            }
        }

        listState.OnBottomReached(loadMoreEnabled, onLoadMore)
    }
}

@Composable
fun LazyListState.OnBottomReached(
    loadMoreEnabled: Boolean,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            loadMoreEnabled && lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow {
            shouldLoadMore.value
        }.collect { loadMoreEnabled ->
            if (loadMoreEnabled) onLoadMore()
        }
    }
}

@Composable
fun ItemMovie(
    movie: Movie
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = movie.overview,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun ItemLoading() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary
        )
    }
}

@Preview
@Composable
fun MoviesUiPreview() {
    AppTheme {
        MoviesUi(FakeMoviesComponent())
    }
}

class FakeMoviesComponent : MoviesComponent {

    override val moviesState: PagedLoading.State<Movie> = PagedLoading.State.Data(
        0,
        getFakeMovies(),
        PagedLoading.DataStatus.Normal
    )

    override fun onPullToRefresh() {}

    override fun onRetryClicked() {}

    override fun onLoadMore() {}

    private fun getFakeMovies(): List<Movie> {
        val list = arrayListOf<Movie>()
        repeat(13) {
            list.add(Movie(it))
        }
        return list.toList()
    }
}