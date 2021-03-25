package me.aartikov.sesamesample.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.LifecycleOwner
import me.aartikov.sesame.activable.bindToLifecycle
import me.aartikov.sesame.dialog.DialogObserver
import me.aartikov.sesame.form.view.InputObserver
import me.aartikov.sesame.navigation.NavigationMessageDispatcher
import me.aartikov.sesame.navigation.bind
import me.aartikov.sesame.property.PropertyObserver
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseFragment<VM : BaseViewModel>(
    @LayoutRes contentLayoutId: Int,
    vmClass: KClass<VM>
) : Fragment(contentLayoutId), PropertyObserver, DialogObserver, InputObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    override val dialogObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    val vm: VM by createViewModelLazy(vmClass, { viewModelStore })

    @Inject
    internal lateinit var navigationMessageDispatcher: NavigationMessageDispatcher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.bindToLifecycle(viewLifecycleOwner.lifecycle)

        vm.navigationMessageQueue.bind(navigationMessageDispatcher, node = this, viewLifecycleOwner)

        vm.showError bind {
            Toast.makeText(requireContext(), it.resolve(requireContext()), Toast.LENGTH_SHORT).show()
        }
    }

    fun onBackPressed() {
        vm.onBackPressed()
    }
}