package me.aartikov.sesamecomposesample.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.aartikov.sesame.compose.form.control.CheckControl
import me.aartikov.sesame.compose.form.control.InputControl
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.menu.MenuButton
import me.aartikov.sesamecomposesample.theme.AppTheme
import me.aartikov.sesamecomposesample.utils.resolve

@Composable
fun FormUi(
    component: FormComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {

                CommonTextField(component.nameInput, R.string.name_hint)

                CommonTextField(component.emailInput, R.string.email_hint)

                CommonTextField(component.phoneInput, R.string.phone_hint)

                PasswordField(component.passwordInput, R.string.password_hint)

                PasswordField(component.confirmPasswordInput, R.string.confirm_password_hint)

                CheckboxField(component.termsCheckBox)

                MenuButton(
                    text = stringResource(R.string.submit_button),
                    onClick = component::onSubmitClicked
                )
            }
        }
    }
}


@Composable
fun CommonTextField(inputControl: InputControl, @StringRes label: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val textState by remember { inputControl.text }
        val invalidInput by derivedStateOf {
            inputControl.error.value != null
        }
        
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    inputControl.onFocusChanged(it.isFocused)
                },
            value = textState,
            keyboardOptions = inputControl.keyboardOptions,
            singleLine = inputControl.singleLine,
            label = { Text(stringResource(label)) },
            onValueChange = { inputControl.onTextChanged(it) },
            isError = invalidInput,
            visualTransformation = inputControl.transformer,
        )

        Text(
            text = inputControl.error.value?.resolve() ?: LocalizedString.empty().resolve(),
            style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.error),
        )
    }
}

@Composable
fun PasswordField(inputControl: InputControl, @StringRes label: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val textState by remember { inputControl.text }
        val invalidInput by derivedStateOf { inputControl.error.value != null }
        val passwordVisibility = remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textState,
            keyboardOptions = inputControl.keyboardOptions,
            singleLine = inputControl.singleLine,
            label = { Text(stringResource(label)) },
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisibility.value)
                    Icons.Filled.VisibilityOff
                else Icons.Filled.Visibility

                IconButton(onClick = {
                    passwordVisibility.value = !passwordVisibility.value
                }) {
                    Icon(imageVector = image, null)
                }
            },
            isError = invalidInput,
            onValueChange = { inputControl.onTextChanged(it) }
        )

        Text(
            text = inputControl.error.value?.resolve() ?: LocalizedString.empty().resolve(),
            style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.error),
        )
    }
}


@Composable
fun CheckboxField(checkControl: CheckControl) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val isChecked by remember { checkControl.checked }
        val isEnabled by remember { checkControl.enabled }

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { checkControl.onCheckedChanged(it) },
                enabled = isEnabled
            )
            Text(text = stringResource(R.string.terms_hint))
        }

        Text(
            text = checkControl.error.value?.resolve() ?: LocalizedString.empty().resolve(),
            style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.error),
        )
    }
}

@Preview
@Composable
fun FormUiPreview() {
    AppTheme {
        FormUi(FakeFormComponent())
    }
}


class FakeFormComponent : FormComponent {

    override val nameInput = InputControl(
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
    )

    override val emailInput = InputControl(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )

    override val phoneInput = InputControl(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    )

    override val passwordInput = InputControl(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )

    override val confirmPasswordInput = InputControl(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )

    override val termsCheckBox = CheckControl()

    override fun onSubmitClicked() = Unit
}
