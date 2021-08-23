package me.aartikov.sesame.loading.paged

/**
 * Page with data for [PagedLoading]
 */
data class Page<T : Any>(
    val data: List<T>,
    val hasNextPage: Boolean
)