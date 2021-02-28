# Property

Provides observable properties and one-time commands.

## Core concepts

*Observable property* - a property that notifies about its changes.

*Computed property* - a read-only observable property that is automatically kept in sync with other observable properties.

*Command* - one-time action such as showing a toast or an error dialog.

*Property host* - a class that contains observable properties and commands.

*Property observer* - a class that observes properties and handles commands.

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

5. Bind UI to View Model:
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

## What is wrong with StateFlow?
[StateFlow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) is an observable data holder. It works fine, but the syntax is not ideal for View Model's properties. The goal of Sesame property component is to improve the syntax.

Compare:
```kotlin
    val count: StateFlow<Int> get() = _count
    private val _count = MutableStateFlow(0)
    
    _count.value++
```

with:
```kotlin
    var count by state(0)
        private set
    
    count++
```

By the way, Sesame properties use `StateFlow` under the hood, so this is not competition but cooperation.

## Interop with StateFlow
To convert a `StateFlow` to an observable property use `stateFromFlow` method.
To get a `StateFlow` from an observable property use `::someProperty.flow` syntax.