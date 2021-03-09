package me.aartikov.sesamesample.clock

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentClockBinding

@AndroidEntryPoint
class ClockFragment : BaseFragment<ClockViewModel>(R.layout.fragment_clock, ClockViewModel::class) {

    private val binding by viewBinding(FragmentClockBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            vm::formattedTime bind time::setText
        }
    }
}