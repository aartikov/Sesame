package me.aartikov.sesamecomposesample.form

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import me.aartikov.sesame.compose.form.control.CheckControl
import me.aartikov.sesame.compose.form.control.InputControl
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.menu.MenuButton
import me.aartikov.sesamecomposesample.theme.AppTheme
import me.aartikov.sesamecomposesample.utils.resolve
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

@Composable
fun FormUi(
    component: FormComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {

                CommonTextField(
                    modifier,
                    component.nameInput,
                    stringResource(id = R.string.name_hint)
                )

                CommonTextField(
                    modifier,
                    component.emailInput,
                    stringResource(id = R.string.email_hint)
                )

                CommonTextField(
                    modifier,
                    component.phoneInput,
                    stringResource(id = R.string.phone_hint)
                )

                PasswordField(
                    modifier,
                    component.passwordInput,
                    stringResource(id = R.string.password_hint)
                )

                PasswordField(
                    modifier,
                    component.confirmPasswordInput,
                    stringResource(id = R.string.confirm_password_hint)
                )

                CheckboxField(
                    modifier,
                    component.termsCheckBox,
                    stringResource(id = R.string.terms_hint)
                )

                MenuButton(
                    text = stringResource(R.string.submit_button),
                    onClick = component::onSubmitClicked,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = component.submitButtonState.color),
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (component.konfettiState == KonfettiState.Shown) {
                KonfettiWidget(maxWidth)
            }
        }
    }
}

@Composable
fun CommonTextField(
    modifier: Modifier = Modifier,
    inputControl: InputControl,
    label: String
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val focusRequester = remember { FocusRequester() }

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

        Text(
            text = inputControl.error?.resolve() ?: "",
            style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.error)
        )
    }
}

@Composable
fun KonfettiWidget(width: Dp) {
    val colors = listOf(
        colorResource(id = R.color.orange).toArgb(),
        colorResource(id = R.color.purple).toArgb(),
        colorResource(id = R.color.pink).toArgb(),
        colorResource(id = R.color.red).toArgb()
    )

    val widthPx = with(LocalDensity.current) { width.toPx() }

    AndroidView(factory = { ctx ->
        KonfettiView(ctx).apply {
            build()
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
    })
}


@Composable
fun CheckboxField(modifier: Modifier = Modifier, checkControl: CheckControl, label: String) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = checkControl.checked,
                onCheckedChange = { checkControl.onCheckedChanged(it) },
                enabled = checkControl.enabled
            )

            Text(text = label)
        }

        Text(
            text = checkControl.error?.resolve() ?: "",
            style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.error),
        )
    }
}

@Composable
fun PasswordField(modifier: Modifier = Modifier, inputControl: InputControl, label: String) {
    Column(modifier = modifier.fillMaxWidth()) {
        val focusRequester = remember { FocusRequester() }
        var passwordVisibility by remember { mutableStateOf(false) }

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

        Text(
            text = inputControl.error?.resolve() ?: "",
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

    override val submitButtonState = SubmitButtonState.Valid

    override val konfettiState = KonfettiState.Shown

    override fun onSubmitClicked() = Unit
}
