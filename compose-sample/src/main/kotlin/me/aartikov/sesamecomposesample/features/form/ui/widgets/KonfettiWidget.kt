package me.aartikov.sesamecomposesample.features.form.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.aartikov.sesamecomposesample.R
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


@Composable
fun KonfettiWidget(
    width: Dp,
    dropKonfettiEvent: Flow<Unit>,
    modifier: Modifier = Modifier
) {

    val widthPx = with(LocalDensity.current) { width.toPx() }
    val scope = rememberCoroutineScope()
    val colors = listOf(
        colorResource(id = R.color.orange).toArgb(),
        colorResource(id = R.color.purple).toArgb(),
        colorResource(id = R.color.pink).toArgb(),
        colorResource(id = R.color.red).toArgb()
    )

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val view = KonfettiView(context)

            scope.launch {
                dropKonfettiEvent.collectLatest {
                    view
                        .build()
                        .addColors(colors)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.Square, Shape.Circle)
                        .addSizes(Size(12))
                        .setPosition(-50f, widthPx + 50f, -50f, -50f)
                        .streamFor(300, 5000L)
                }
            }

            view
        },
    )
}
