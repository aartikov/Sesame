package me.aartikov.lib.navigation

interface NodeWalker {
    fun getNextNode(node: Any): Any?
}