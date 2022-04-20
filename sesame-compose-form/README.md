# Compose form
Provides input field validation for Jetpack Compose projects.

## Controls
Controls are building blocks for validatable forms. Sesame provides `InputControl` to input text values and `CheckControl` to input boolean values. Controls are logical representation of UI elements. They should be created in ViewModel (or [Decompose](https://github.com/arkivanov/Decompose) component) and bound to Jetpack Compose UI.

1. Create controls. There are plenty of settings in `InputControl` (see [the sample](https://github.com/aartikov/Sesame/blob/master/compose-sample/src/main/kotlin/me/aartikov/sesamecomposesample/features/form/ui/RealFormComponent.kt) for more details).

```kotlin
class FormViewModel : ViewModel() {
    ...
    
    val nameInput = InputControl(
        maxLength = NAME_MAX_LENGTH,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
        )
    )
    
    val termsCheckBox = CheckControl()
}
```

1. Add UI (see implementation of `TextField` and `CheckboxField` in [the sample](https://github.com/aartikov/Sesame/blob/master/compose-sample/src/main/kotlin/me/aartikov/sesamecomposesample/features/form/ui/widgets/)):
```kotlin
    TextField(
        viewModel.nameInput,
        label = stringResource(id = R.string.name_hint)
    )
    
    CheckboxField(
        viewModel.termsCheckBox,
        label = stringResource(id = R.string.terms_hint)
    )
```

After it is done controls and UI will be bound with two-way binding. When you change `nameInput.text` from code the text in `TextField` will be updated accordingly. And vice verce, if an user enters some text to `TextField` then `nameInput.text` will be updated.

## Form validation

1. Create a form validator in ViewModel (see more complex validation in [the sample](https://github.com/aartikov/Sesame/blob/master/compose-sample/src/main/kotlin/me/aartikov/sesamecomposesample/features/form/ui/RealFormComponent.kt)):

```kotlin
    private val formValidator = viewModelScope.formValidator {

        input(nameInput) {
            isNotBlank(R.string.field_is_blank_error_message)
        }

        checked(termsCheckBox, R.string.terms_are_accepted_error_message)
    }
```

2. Call validation:
```kotlin
    fun onSubmitClicked() {
        val result = formValidator.validate()
        if (result.isValid) {
            // Do something
        }
    }
```
If the result is invalid then errors will be displayed in the corresponding controls.

## Additional features
By default `formValidator` validates inputs only when `validate()` is called and it does not clear errors when inputs are changed. You can enhance this behaviour with `FormValidationFeature`s.

```kotlin
    private val formValidator = viewModelScope.formValidator {
    
        features = listOf(
            ValidateOnFocusLost,
            RevalidateOnValueChanged,
            SetFocusOnFirstInvalidControlAfterValidation
        )

        ...
    }
```

This form validator:
* validates an input field automatically when focus is lost
* revalidates an input field with error when its value is changed
* brings a focus to the first invalid field when `validate()` is called


## Dynamic validation result
`dynamicValidationResult` allows to сontinuously monitor a validation state. For example, you can enable a button only when a form is valid.

```kotlin
    private val validationResult by viewModelScope.dynamicValidationResult(formValidator)
    
    val submitButtonEnabled by derivedStateOf { validationResult.isValid } 

```