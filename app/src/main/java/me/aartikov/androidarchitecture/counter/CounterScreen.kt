package me.aartikov.androidarchitecture.counter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_counter.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen

@AndroidEntryPoint
class CounterScreen : BaseScreen<CounterViewModel>(R.layout.screen_counter, CounterViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        minusButton.setOnClickListener { vm.onMinusButtonClicked() }
        plusButton.setOnClickListener { vm.onPlusButtonClicked() }

        vm::count bind { countTextView.text = it.toString() }
        vm::minusButtonEnabled bind minusButton::setEnabled
        vm::plusButtonEnabled bind plusButton::setEnabled

        vm.showMessageCommand bind { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
    }
}

