package me.aartikov.lib.loading.simple

interface Emptyable {
    fun isEmpty(): Boolean
}

fun dataIsEmpty(data: Any): Boolean {
    return when (data) {
        is Collection<*> -> data.isEmpty()
        is Array<*> -> data.isEmpty()
        is Emptyable -> data.isEmpty()
        else -> false
    }
}