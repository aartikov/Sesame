package me.aartikov.androidarchitecture.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.LifecycleOwner
import me.aartikov.lib.data_binding.PropertyObserver
import me.aartikov.lib.navigation.NavigationMessageDispatcher
import me.aartikov.lib.widget.WidgetObserver
import javax.inject.Inject
import kotlin.reflect.KClass

open class BaseScreen<VM : BaseViewModel>(
    @LayoutRes contentLayoutId: Int,
    vmClass: KClass<VM>
) : Fragment(contentLayoutId), PropertyObserver, WidgetObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    override val widgetObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    val vm: VM by createViewModelLazy(vmClass, { viewModelStore })

    @Inject
    internal lateinit var navigationMessageDispatcher: NavigationMessageDispatcher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.navigate bind {
            navigationMessageDispatcher.dispatch(it, firstNode = this)
        }

        vm.showError bind {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }
}