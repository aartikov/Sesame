package me.aartikov.sesamesample.counter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentCounterBinding

@AndroidEntryPoint
class CounterFragment : BaseFragment<CounterViewModel>(R.layout.fragment_counter, CounterViewModel::class) {

    private val binding by viewBinding(FragmentCounterBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            minusButton.setOnClickListener { vm.onMinusButtonClicked() }
            plusButton.setOnClickListener { vm.onPlusButtonClicked() }

            vm::count bind { count.text = it.toString() }
            vm::minusButtonEnabled bind minusButton::setEnabled
            vm::plusButtonEnabled bind plusButton::setEnabled

            vm.showMessage bind {
                Toast.makeText(requireContext(), it.resolve(requireContext()), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

