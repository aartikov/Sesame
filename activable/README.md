# Activable

Equips View Models with a very simple lifecycle.

## Why View Model needs lifecycle?
Despite that View Model should be abstracted away from the coresponding View (Activity or Fragment) it still needs to be aware of Android lifecycle. It is the common case when View Model subscribes to a Repository in order to update UI whenever something is changed in a database. But it is wasteful to keep a database subcription when a screen is placed to a backstack or the whole appilication is in background. It is better to subscribe and unsubscribe according to a lifecycle.

## How complex a lifecycle should be?
Activity and Fragment lifecycles are quite complex. There are `onCreate`, `onStart`, `onResume`, `onPause`, `onStop` and `onDestroy` callbacks. Sesame  activable component is build on asumption that View Model doesn't need such a complex lifecycle.  
First of all it doesn't need `onCreate` and `onDestroy`. Any initialization can be done in `init`-block instead of `onCreate`. `onDestroy` is required to clean up something. In View Models we typically cancel started coroutines and unsubscribe from flows in the clean-up phase. All of these are made automatically with help of `CoroutineScope`.  
`onResume`-`onPause` and `onStart`-`onStop` are different callback pairs in Android. But View Model can be abstract enougth to ignore the difference. In other words View Model is only interested if a screen is active. The such simple lifecycle has just two methods: `onActive` and `onInactive`.

## Make View Model activable
1. Implement `Activable` interface in a base View Model class. [Kotlin delegation](https://kotlinlang.org/docs/delegation.html) is handy here.
```kotlin
abstract class BaseViewModel : ViewModel(), Activable by Activable() {

}
```

2. Bind activable View Model to Activity/Fragment lifecycle with `bindToLifecycle` method. It translates `onStart` to `onActive` and `onStop` to `onInactive`.

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    vm.bindToLifecycle(viewLifecycleOwner.lifecycle)
}
```

3. Use the lifecycle callbacks in a child class.
```kotlin
class MyViewModel: BaseViewModel() {

    override fun onActive() {
        super.onActive()
        // Do something
    }

    override fun onInactive() {
        // Do something
        super.onInactive()
    }
}
```

## Activable flow
In the section [Why View Model needs lifecycle?](#why-view-model-needs-lifecycle) the common task is described: View Model subscribes to updates from a Repository, subscription should be established only when View Model is active. Sesame helps to solve this task.

Given a flow of updates: 
```
val profileFlow: Flow<Profile> = profileRepository.observeProfile()
```

Use `activableFlow(originalFlow, activable, coroutineScope)` to convert it to an activable flow.

```
val activableProfileFlow = activableFlow(profileFlow, this, viewModelScope)
```

An activable flow collects the original flow and emits elements further when the activable is active. It stops collecting and emitting when the activable is inactive.
