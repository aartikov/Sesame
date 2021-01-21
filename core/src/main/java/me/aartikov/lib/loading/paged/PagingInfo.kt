package me.aartikov.lib.loading.paged

data class PagingInfo<T>(
    val loadedPageCount: Int,
    val loadedItems: List<T>
) {
    val lastItem: T? get() = loadedItems.lastOrNull()
}