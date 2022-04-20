package me.aartikov.sesamecomposesample.features.movies.ui

import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesame.loading.paged.handleErrors
import me.aartikov.sesame.loading.paged.refresh
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.core.message.MessageService
import me.aartikov.sesamecomposesample.core.utils.componentCoroutineScope
import me.aartikov.sesamecomposesample.core.utils.toComposeState
import me.aartikov.sesamecomposesample.features.movies.domain.LoadMoviesPageInteractor
import me.aartikov.sesamecomposesample.features.movies.domain.Movie

class RealMoviesComponent(
    componentContext: ComponentContext,
    private val loadMoviesPageInteractor: LoadMoviesPageInteractor,
    private val messageService: MessageService
) : ComponentContext by componentContext, MoviesComponent {

    private val coroutineScope = componentCoroutineScope()

    private val moviesLoading = PagedLoading<Movie>(
        coroutineScope,
        loadPage = { loadMoviesPageInteractor.execute(it.loadedData.size) }
    )

    override val moviesState by moviesLoading.stateFlow.toComposeState(coroutineScope)

    init {
        moviesLoading.handleErrors(coroutineScope) { error ->
            if (error.hasData) {
                val message = error.throwable.message
                messageService.showMessage(LocalizedString.raw(message ?: "Error"))
            }
        }

        moviesLoading.refresh()
    }

    override fun onPullToRefresh() {
        moviesLoading.refresh()
    }

    override fun onRetryClicked() {
        moviesLoading.refresh()
    }

    override fun onLoadMore() {
        moviesLoading.loadMore()
    }
}