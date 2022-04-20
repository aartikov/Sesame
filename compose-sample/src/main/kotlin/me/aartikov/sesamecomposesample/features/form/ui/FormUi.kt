package me.aartikov.sesamecomposesample.features.form.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.aartikov.sesame.compose.form.control.CheckControl
import me.aartikov.sesame.compose.form.control.InputControl
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.core.theme.AppTheme
import me.aartikov.sesamecomposesample.features.form.ui.widgets.CheckboxField
import me.aartikov.sesamecomposesample.features.form.ui.widgets.KonfettiWidget
import me.aartikov.sesamecomposesample.features.form.ui.widgets.PasswordTextField
import me.aartikov.sesamecomposesample.features.form.ui.widgets.TextField
import me.aartikov.sesamecomposesample.features.menu.ui.MenuButton

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
                TextField(
                    component.nameInput,
                    label = stringResource(id = R.string.name_hint),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                TextField(
                    component.emailInput,
                    label = stringResource(id = R.string.email_hint),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                TextField(
                    component.phoneInput,
                    label = stringResource(id = R.string.phone_hint),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                PasswordTextField(
                    component.passwordInput,
                    label = stringResource(id = R.string.password_hint),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                PasswordTextField(
                    component.confirmPasswordInput,
                    label = stringResource(id = R.string.confirm_password_hint),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                CheckboxField(
                    component.termsCheckBox,
                    label = stringResource(id = R.string.terms_hint)
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
