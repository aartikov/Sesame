package me.aartikov.sesame.loading.paged

/**
 * Stores information about already loaded data for [PagedLoading].
 * @property loadedData sequentially merged data of all loaded pages.
 */
data class PagingInfo<T>(
    val loadedData: List<T>
) {
    val lastItem: T? get() = loadedData.lastOrNull()
}