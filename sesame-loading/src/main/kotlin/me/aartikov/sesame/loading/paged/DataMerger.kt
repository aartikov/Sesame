package me.aartikov.sesame.loading.paged

/**
 * Allows to specify how stored data is merged with incoming page data
 */
interface DataMerger<T : Any> {
    fun merge(storedData: List<T>, incomingData: List<T>): List<T>
}

class SimpleDataMerger<T : Any> : DataMerger<T> {
    override fun merge(storedData: List<T>, incomingData: List<T>): List<T> {
        return storedData + incomingData
    }
}

class DuplicateRemovingDataMerger<T : Any>(
    private val keySelector: (T) -> Any
) : DataMerger<T> {

    override fun merge(storedData: List<T>, incomingData: List<T>): List<T> {
        val storedKeys = storedData.map(keySelector).toHashSet()
        return storedData + incomingData.filter { keySelector(it) !in storedKeys }
    }
}