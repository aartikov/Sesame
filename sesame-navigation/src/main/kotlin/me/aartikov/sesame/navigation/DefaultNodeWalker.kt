package me.aartikov.sesame.navigation

import androidx.fragment.app.Fragment

/**
 * The default implementation of [NodeWalker].
 * It iterates up the fragment-activity tree (e.g. from Fragment to it's parent Fragment and then to the Activity).
 */
class DefaultNodeWalker : NodeWalker {

    override fun getNextNode(node: Any): Any? {
        return if (node is Fragment) {
            node.parentFragment ?: node.activity
        } else {
            null
        }
    }
}