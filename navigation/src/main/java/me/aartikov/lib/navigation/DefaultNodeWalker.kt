package me.aartikov.lib.navigation

import androidx.fragment.app.Fragment

class DefaultNodeWalker : NodeWalker {

    override fun getNextNode(node: Any): Any? {
        return if (node is Fragment) {
            node.parentFragment ?: node.activity
        } else {
            null
        }
    }
}