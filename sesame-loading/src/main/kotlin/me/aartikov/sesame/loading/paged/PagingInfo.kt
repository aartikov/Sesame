package me.aartikov.sesame.loading.paged

/**
 * Stores information about already loaded pages for [PagedLoading].
 * @property loadedPageCount count of loaded pages.
 * @property loadedData sequentially merged data of all loaded pages.
 */
data class PagingInfo<T>(
    val loadedPageCount: Int,
    val loadedData: List<T>
) {
    val lastItem: T? get() = loadedData.lastOrNull()
}