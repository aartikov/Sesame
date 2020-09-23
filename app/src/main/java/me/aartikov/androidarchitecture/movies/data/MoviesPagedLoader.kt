package me.aartikov.androidarchitecture.movies.data

import me.aartikov.androidarchitecture.movies.domain.Movie
import me.aartikov.lib.loading.paged.PagedLoader
import me.aartikov.lib.loading.paged.PagingInfo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MoviesPagedLoader @Inject constructor(private val service: MoviesService) : PagedLoader<Movie> {

    override suspend fun loadFirstPage(fresh: Boolean): List<Movie> =
        service.getMovies(0)

    override suspend fun loadNextPage(pagingInfo: PagingInfo<Movie>): List<Movie> =
        service.getMovies(pagingInfo.loadedPageCount)
}