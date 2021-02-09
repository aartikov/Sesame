package me.aartikov.androidarchitecture.counter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.databinding.ScreenCounterBinding

@AndroidEntryPoint
class CounterScreen : BaseScreen<CounterViewModel>(R.layout.screen_counter, CounterViewModel::class) {

    private val binding by viewBinding(ScreenCounterBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            minusButton.setOnClickListener { vm.onMinusButtonClicked() }
            plusButton.setOnClickListener { vm.onPlusButtonClicked() }

            vm::count bind { count.text = it.toString() }
            vm::minusButtonEnabled bind minusButton::setEnabled
            vm::plusButtonEnabled bind plusButton::setEnabled

            vm.showMessage bind { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }
}

