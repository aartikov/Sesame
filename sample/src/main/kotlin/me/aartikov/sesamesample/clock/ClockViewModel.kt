package me.aartikov.sesamesample.clock

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.aartikov.sesame.property.stateFromFlow
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor() : BaseViewModel() {

    private val clockFlow: Flow<String> = flow {
        while (true) {
            val formattedTime = formatTime(Clock.System.now())
            Log.d("Clock", "Current time: $formattedTime")
            emit(formattedTime)
            delay(1000)
        }
    }.toActivableFlow()

    val formattedTime by stateFromFlow("", clockFlow)

    private fun formatTime(time: Instant): String {
        val localTime = time.toLocalDateTime(TimeZone.currentSystemDefault())
        val hours = localTime.hour.toString().padStart(2, '0')
        val minutes = localTime.minute.toString().padStart(2, '0')
        val seconds = localTime.second.toString().padStart(2, '0')
        return "$hours:$minutes:$seconds"
    }
}