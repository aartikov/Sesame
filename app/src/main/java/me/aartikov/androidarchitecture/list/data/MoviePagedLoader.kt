package me.aartikov.androidarchitecture.list.data

import android.util.Log
import me.aartikov.androidarchitecture.list.domain.Movie
import me.aartikov.lib.loading.paged.PagedLoader
import me.aartikov.lib.loading.paged.PagingInfo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MoviePagedLoader @Inject constructor(private val service: MovieService) : PagedLoader<Movie> {

    override suspend fun loadFirstPage(fresh: Boolean): List<Movie> {
        Log.d("MovieLoader", "loadFirstPage() invoked")
        return service.getMovies(0)
    }


    override suspend fun loadNextPage(pagingInfo: PagingInfo<Movie>): List<Movie> {
        Log.d("MovieLoader", "loadNextPage() invoked")
        return service.getMovies(pagingInfo.loadedPageCount / 10)
    }
}