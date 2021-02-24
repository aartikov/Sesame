package me.aartikov.sesamesample

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentNavigator(
    @IdRes val containerId: Int,
    val fm: FragmentManager
) {

    private val Fragment.screenTag get() = this::class.java.name

    val currentScreen: Fragment?
        get() = fm.findFragmentById(containerId)

    fun setRoot(fragment: Fragment) {
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        fm.beginTransaction()
            .addToBackStack(fragment.screenTag)
            .add(containerId, fragment, fragment.screenTag)
            .commit()

        fm.executePendingTransactions()
    }

    fun goTo(fragment: Fragment) {
        fm.beginTransaction()
            .addToBackStack(fragment.screenTag)
            .replace(containerId, fragment, fragment.screenTag)
            .commit()

        fm.executePendingTransactions()
    }

    fun replace(fragment: Fragment) {
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        }
        goTo(fragment) // calls executePendingTransactions
    }

    fun back(): Boolean {
        if (fm.backStackEntryCount > 1) {
            fm.popBackStackImmediate()
            return true
        } else {
            return false
        }
    }

    inline fun <reified T : Fragment> backTo() {
        fm.popBackStackImmediate(T::class.java.name, 0)
    }

    inline fun <reified T : Fragment> hasScreen(): Boolean {
        return fm.findFragmentByTag(T::class.java.name) != null
    }
}
