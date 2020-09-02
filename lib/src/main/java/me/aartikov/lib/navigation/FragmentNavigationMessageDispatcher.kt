package me.aartikov.lib.navigation

import androidx.fragment.app.Fragment

class FragmentNavigationMessageDispatcher(
    fragment: Fragment
) : NavigationMessageDispatcher(fragment) {

    override fun getParent(node: Any?): Any? {
        return if (node is Fragment) {
            node.parentFragment ?: node.activity
        } else {
            null
        }
    }
}