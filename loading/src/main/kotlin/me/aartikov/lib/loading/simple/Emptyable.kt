package me.aartikov.lib.loading.simple

/**
 * Allows to customize a behaviour of [isEmpty] for custom classes.
 */
interface Emptyable {
    fun isEmpty(): Boolean
}

/**
 * Checks if [data] is empty. It is used by [Loading] to choose from [Loading.State.Empty] and [Loading.State.Data].
 */
fun isEmpty(data: Any): Boolean {
    return when (data) {
        is Collection<*> -> data.isEmpty()
        is Array<*> -> data.isEmpty()
        is ByteArray -> data.isEmpty()
        is CharArray -> data.isEmpty()
        is ShortArray -> data.isEmpty()
        is IntArray -> data.isEmpty()
        is LongArray -> data.isEmpty()
        is FloatArray -> data.isEmpty()
        is DoubleArray -> data.isEmpty()
        is BooleanArray -> data.isEmpty()
        is Emptyable -> data.isEmpty()
        else -> false
    }
}