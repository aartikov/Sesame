# Property

This component provides observable properties and one-time commands.

## Core concepts

*Observable property* - a property that notifies about its changes.

*Computed property* - a read-only observable property that is automatically kept in sync with other observable properties.

*Command* - one-time action such as showing a toast or an error dialog.

*Property host* - a class that can contain observable properties and commands.

*Property observer* - a class that can observe observable properties and handle commands.

## How to use

1. Implement `PropertyHost` interface in `ViewModel`:

```kotlin
class CounterViewModel : ViewModel(), PropertyHost {

    override val propertyHostScope: CoroutineScope get() = viewModelScope
    
    ...
}
```

2. Declare observable properties and commands:

```kotlin
    var count by state(0)
        private set

    val plusButtonEnabled by computed(::count) { it < MAX_COUNT }
    
    val showMessage = command<String>()
```

3. Change properties and send commands when it is required:
```kotlin
    fun onPlusButtonClicked() {
        if (plusButtonEnabled) {
            count++
        }

        if (count == MAX_COUNT) {
            showMessage("It's enough!")
        }
    }
```

4. Implement `PropertyObserver` in `Activity` or `Fragment`:
```kotlin
class CounterFragment : Fragment(), PropertyObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    
    ...
}
```

5. Bind properties and commands to UI updates:
```kotlin
    private val binding by viewBinding(FragmentCounterBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            plusButton.setOnClickListener { vm.onPlusButtonClicked() }

            vm::count bind { count.text = it.toString() }
            vm::plusButtonEnabled bind plusButton::setEnabled

            vm.showMessage bind { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }
```

## Autorun
Sometimes it is required to run some code in a `PropertyHost` whenever certain properties are changed. You can do it with `autorun`:

```
    init {
        autorun(::count) {
            Log.d("Counter", "Count is changed: $it")
        }
    }
```

## Interop with StateFlow
Sesame observable properties use [StateFlow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) under the hood.
To convert a `StateFlow` to an observable property use `stateFromFlow` method.
To get a `StateFlow` from an observable property use `::someProperty.flow` extension property.