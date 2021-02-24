package me.aartikov.sesamesample.dialogs

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseScreen
import me.aartikov.sesamesample.databinding.ScreenClockBinding

@AndroidEntryPoint
class ClockScreen : BaseScreen<ClockViewModel>(R.layout.screen_clock, ClockViewModel::class) {

    private val binding by viewBinding(ScreenClockBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            vm::formattedTime bind time::setText
        }
    }
}