# Navigation

Gives an universal way to navigate between screens.

## What problems it solves?
Organizing navigation in Android applications is not a straightforward task. There are difficulties that are common for most applications:

1. **Calling navigation from View Models is not simple.**
   View Models are good classes to place navigation logic. Activity and FragmentManager are required to implement navigation calls. But it is a bad idea to reference these classes from View Models. It causes memory leaks and undesired dependencies.
   
2. **Fragment transanctions can cause crashes.**
   There are limitations when a fragment transaction can be executed.
   Calling `commit` after `onSaveInstanceState` causes `IllegalStateException: "Can not perform this action after onSaveInstanceState"`. Commiting a transaction when the other one is not finished yet causes another exception `IllegalStateException: "FragmentManager is already executing transactions"`.
   It is not simple to control that transactions are executed at a correct time because often navigation is required as completition of some asynchronous operation.

3. **Nested navigation is hard to implement.**
   Navigation can be nested. Some navigation calls affect the inner screen container, other calls - the outer one. View Models ideally should not be aware of this logic.
   
## How it works?
Instead of calling navigation methods directly Sesame provides navigation messages - a marker interface `NavigationMessage`. View Model sends navigation messages by enqueuing them to `NavigationMessageQueue`. On the Activity/Fragment side navigation messages are passed to `NavigationMessageDispatcher`. The dispatcher handles messages, but it doesn't do it by itself. It delegates this task to `NavigationMessageHandler`s. A `NavigationMessageHandler` is responsible for navigation execution (starting activities or commiting fragment transactions). There could be several navigation message handlers. In that case they work like a chain - if some handler doesn't handles a message then the message is passed futher.

## How it solves problems?

1. `NavigationMessage` and `NavigationMessageQueue` allow to call navigation from View Models.

2. It is guaranteed that fragment transactions are commited when it is legal. `NavigationMessageQueue` passes messages only when the coresponding Activity/Fragment is in resumed state. `NavigationMessageDispatcher` has an internal queue to handle messages sequentially.

3. Nested navigation is solved by `NavigationMessageHandler`s. The typical Android hierarchy is [Fragment] -> [parent Fragment] -> [Activity]. The parent Fragment and Activity implement `NavigationMessageHandler`, so different messages are handled on different levels.

## How to use

1. Declare some navigation messages.
```kotlin
object Back : NavigationMessage
object OpenProfileScreen : NavigationMessage
```

2. Use `NavigationMessageQueue` in View Model to send messages.
```kotlin
class MenuViewModel : ViewModel() {

    val navigationMessageQueue = NavigationMessageQueue()

    fun onBackPressed() {
        navigationMessageQueue.send(Back)
    }

    fun onProfileButtonClicked() {
        navigationMessageQueue.send(OpenProfileScreen)
    }
}
```

3. Setup `NavigationMessageDispatcher` in activities and fragments. There should be a single instance of this class per Activity.
```kotlin
class MainActivity : AppCompatActivity() {

    @Inject
    internal lateinit var navigationMessageDispatcher: NavigationMessageDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        navigationMessageDispatcher.attach(this)
        ...
    }
}
```

```kotlin
class MenuFragment: Fragment() {

    @Inject
    internal lateinit var navigationMessageDispatcher: NavigationMessageDispatcher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        vm.navigationMessageQueue.bind(navigationMessageDispatcher, node = this, viewLifecycleOwner)
        ...
    }
}
```

4. Implement `NavigationMessageHandler`. Return value of `handleNavigationMessage` indicates if a message was handled. If there is only one `NavigationMessageHandler` all messages should be handled there. The implementation uses a [wrapper](https://github.com/aartikov/Sesame/blob/readme/sample/src/main/kotlin/me/aartikov/sesamesample/FragmentNavigator.kt) on top of FragmentManager to execute fragment transactions but `NavController` from Android Achitecture Components can be used as well.
```kotlin
class MainActivity : AppCompatActivity(), NavigationMessageHandler {
    
    ...
    
    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is Back -> {
                val success = navigator.back()
                if (!success) finish()
            }
            is OpenProfileScreen -> navigator.goTo(ProfileFragment())
        }
        return true
    }
}
```