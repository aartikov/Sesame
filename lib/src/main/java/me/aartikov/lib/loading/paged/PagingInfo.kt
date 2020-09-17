package me.aartikov.lib.loading.paged

data class PagingInfo<T>(
    val loadedItems: List<T>,
    val loadedPageCount: Int
) {

    val lastItem: T? get() = loadedItems.lastOrNull()
}