package me.aartikov.lib.core.loading.paged

data class PagingInfo<T>(
    val loadedPageCount: Int,
    val loadedItems: List<T>
) {
    val lastItem: T? get() = loadedItems.lastOrNull()
}