package me.aartikov.sesamecomposesample.form

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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
            KonfettiWidget(maxWidth, component.dropKonfettiEvent, modifier)

            val scrollState = rememberScrollState()
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp, horizontal = 8.dp)
            ) {
                CommonTextField(
                    modifier.padding(horizontal = 8.dp),
                    component.nameInput,
                    stringResource(id = R.string.name_hint)
                )

                CommonTextField(
                    modifier.padding(horizontal = 8.dp),
                    component.emailInput,
                    stringResource(id = R.string.email_hint)
                )

                CommonTextField(
                    modifier.padding(horizontal = 8.dp),
                    component.phoneInput,
                    stringResource(id = R.string.phone_hint)
                )

                PasswordField(
                    modifier.padding(horizontal = 8.dp),
                    component.passwordInput,
                    stringResource(id = R.string.password_hint)
                )

                PasswordField(
                    modifier.padding(horizontal = 8.dp),
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
        }
    }
}

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
        val hasFocus by derivedStateOf { inputControl.hasFocus }

        val focusRequester = remember { FocusRequester() }

        if (hasFocus) {
            focusRequester.requestFocus()
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

@Composable
fun KonfettiWidget(width: Dp, dropKonfettiEvent: Flow<Unit>, modifier: Modifier = Modifier) {

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckboxField(
    modifier: Modifier = Modifier,
    checkControl: CheckControl,
    label: String
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(modifier = modifier.fillMaxWidth()) {

        LaunchedEffect(key1 = checkControl) {
            checkControl.scrollToItEvent.collectLatest {
                bringIntoViewRequester.bringIntoView()
            }
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkControl.checked,
                onCheckedChange = { checkControl.onCheckedChanged(it) },
                enabled = checkControl.enabled
            )

            Text(text = label)
        }

        ErrorText(
            checkControl.error?.resolve() ?: "",
            paddingValues = PaddingValues(horizontal = 16.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PasswordField(
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

        val hasFocus by derivedStateOf { inputControl.hasFocus }

        var passwordVisibility by remember { mutableStateOf(false) }

        if (hasFocus) {
            focusRequester.requestFocus()
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

@Composable
fun ErrorText(
    errorText: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = 8.dp)
) {
    Text(
        modifier = modifier.padding(paddingValues),
        text = errorText,
        style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.error),
    )
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

    override val dropKonfettiEvent: Flow<Unit> = flow { }

    override fun onSubmitClicked() = Unit
}
