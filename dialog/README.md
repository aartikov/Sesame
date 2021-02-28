# Dialog

Allows to control dialogs from View Models.

## How it is works?
The component provides `DialogControl` class. This class stores a dialog state and allows to control it. Created in `ViewModel` an instance of `DialogControl` survives configuration changes. A View just displays a current state of `DialogControl` with a usual `AlertDialog`. There is no need to use `FragmentDialog` with this approach.


## How to use

1. Use `dialogControl` function to create `DialogControl` in `ViewModel`. The first type parameter of this function is a type of data to display in a dialog, the second one is a type of result value. Both can be `Unit` if you don't need it.

```kotlin
enum class DialogResult { OK, CANCEL }

val dialog = dialogControl<String, DialogResult>()
```

2. To just show a dialog call:
```kotlin
dialog.show("Some message")
```

3. To show a dialog to receive some user input use `showForResult`. It is a suspend method, so must be called in a `launch`-block. It waits for a user input and returns it. Returned `null` means that the dialog was dismissed.
```kotlin
viewModelScope.launch {
    val result = dialog.showForResult("Some message for result")
    // use result
}
```

4. Implement `DialogObserver` in `Activity` or `Fragment`.
```kotlin
class DialogsFragment : Fragment(), DialogObserver {

    override val dialogObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner

    ...
}
```

5. Bind a dialog control to display a dialog and handle button clicks:
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    vm.dialog bind { message, dc ->
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title)
            .setMessage(message)
            .setPositiveButton(R.string.ok_button) { _, _ ->
                dc.sendResult(DialogResult.OK)
            }
            .setNegativeButton(R.string.cancel_button) { _, _ ->
                dc.sendResult(DialogResult.CANCEL)
            }
            .create()
    }
}
```
