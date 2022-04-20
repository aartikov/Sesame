package me.aartikov.sesamecomposesample.features.form.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import kotlinx.coroutines.flow.collectLatest
import me.aartikov.sesame.compose.form.control.InputControl
import me.aartikov.sesamecomposesample.core.utils.resolve

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PasswordTextField(
    inputControl: InputControl,
    label: String,
    modifier: Modifier = Modifier
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        val focusRequester = remember { FocusRequester() }

        var passwordVisibility by remember { mutableStateOf(false) }

        if (inputControl.hasFocus) {
            SideEffect {
                focusRequester.requestFocus()
            }
        }

        LaunchedEffect(key1 = inputControl) {
            inputControl.scrollToItEvent.collectLatest {
                bringIntoViewRequester.bringIntoView()
            }
        }

        OutlinedTextField(
            value = inputControl.text,
            keyboardOptions = inputControl.keyboardOptions,
            singleLine = inputControl.singleLine,
            label = { Text(text = label) },
            isError = inputControl.error != null,
            onValueChange = inputControl::onTextChanged,
            visualTransformation = if (passwordVisibility) {
                inputControl.visualTransformation
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val image = if (passwordVisibility) {
                    Icons.Filled.VisibilityOff
                } else {
                    Icons.Filled.Visibility
                }

                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(imageVector = image, null)
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    inputControl.onFocusChanged(it.isFocused)
                }
        )

        ErrorText(inputControl.error?.resolve() ?: "")
    }
}