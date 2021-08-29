package me.aartikov.sesamecomposesample.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import me.aartikov.sesame.localizedstring.LocalizedString

@Composable
fun LocalizedString.resolve(): String = resolve(LocalContext.current).toString()
