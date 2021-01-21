package me.aartikov.lib.core.loading.simple

interface Emptyable {
    fun isEmpty(): Boolean
}

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