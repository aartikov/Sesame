package me.aartikov.sesamecomposesample.features.form.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.flow.collectLatest
import me.aartikov.sesame.compose.form.control.InputControl
import me.aartikov.sesamecomposesample.core.utils.resolve

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommonTextField(
    modifier: Modifier = Modifier,
    inputControl: InputControl,
    label: String
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        val focusRequester = remember { FocusRequester() }

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
            onValueChange = inputControl::onTextChanged,
            isError = inputControl.error != null,
            visualTransformation = inputControl.visualTransformation,
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