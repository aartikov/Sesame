# Form
Provides input field validation.

## Controls
Controls are building blocks for validatable forms. Sesame provides `InputControl` to input text values and `CheckControl` to input boolean values. Controls are logical representation of UI elements. They should be created in ViewModel and bound to real UI elements in View.

1. Before creating controls implement `PropertyHost` interface in `ViewModel`:

```kotlin
class FormViewModel : ViewModel(), PropertyHost {

    override val propertyHostScope: CoroutineScope get() = viewModelScope
    
    ...
}
```

2. Create controls. There are plenty of settings in `InputControl` (see [the sample](https://github.com/aartikov/Sesame/tree/master/sesame-form) for more details).

```kotlin
class FormViewModel : ViewModel(), PropertyHost {
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

3. Implement `ControlObserver` in `Activity` or `Fragment`:
```kotlin
class CounterFragment : Fragment(), ControlObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    
    ...
}
``` 

4. Bind controls to views:
```kotlin
    private val binding by viewBinding(FragmentFormBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            vm.nameInput bind nameInputLayout
            vm.termsCheckBox bind termsCheckbox
        }
    }
```

After it is done controls and UI will be bound with two-way binding. When you change `nameInput.text` from code the value in `EditText` will be updated accordingly. And vice verce, if an user enters some text to `EditText` then `nameInput.text` will be updated.

## Form validation

1. Create a form validator in ViewModel (see more complex validation in [the sample](https://github.com/aartikov/Sesame/tree/master/sesame-form)):

```kotlin
    private val formValidator = formValidator {

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
If the result is invalid then errors will be displayed in the corresponding views.

## Additional features
By default `formValidator` validates inputs only when `validate()` is called and it does not clear errors when inputs are changed. You can enhance this behaviour with `FormValidationFeature`s.

```kotlin
    private val formValidator = formValidator {
    
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
    private val validationResult by dynamicValidationResult(formValidator)
    
    val submitButtonEnabled by computed(::validationResult) { it.isValid } 
```