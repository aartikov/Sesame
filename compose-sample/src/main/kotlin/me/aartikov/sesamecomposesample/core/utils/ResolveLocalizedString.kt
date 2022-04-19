package me.aartikov.sesamecomposesample.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import me.aartikov.sesame.localizedstring.LocalizedString

@Composable
fun LocalizedString.resolve(): String {
    LocalConfiguration.current // required to recompose when a locale is changed
    return resolve(LocalContext.current).toString()
}
