# Loop

Provides a simple MVI implementation.

## Core concepts of MVI
The basic principles of any MVI implementation are common. A **state** in MVI is immutable. The only way to change a state is to send an **action**. A new state is producer by a **reducer** - a pure function that receives a previous state and an action.  
But the most interesting part is how **side effects** are handled. Just to remind you, side effects are additional operations: network calls, database queries, etc. This is where implementations can vary. Sesame Loop provides a consise way to handle side effects.

## How does Loop handle side effects?
Loop extends the concept of reducer:

```kotlin
interface Reducer<StateT, ActionT, EffectT> {

    fun reduce(state: StateT, action: ActionT): Next<StateT, EffectT>
}
```

Instead of just `StateT` this reducer returns `Next`:
```kotlin
data class Next<StateT, EffectT>(
   val state: StateT?, 
   val effects: List<EffectT>
)
```

`Next` contains a new state (null means "no changes") and a list of side effects. A side effect does not execute an operation by itself, it is just a description what operation has to be executed. So `reduce` is still a pure function.

To execute side effects `EffectHandler` should be implemented:
```kotlin
interface EffectHandler<in EffectT, out ActionT> {
    suspend fun handleEffect(effect: EffectT, actionConsumer: (ActionT) -> Unit)
}
```

An implementation can emit additional actions with `actionConsumer` callback, for example, to report an operation result.

## How to use?

1. Declare a state, actions and effects.

```kotlin
data class CounterState(
    val count: Int
)

sealed class CounterAction {
    object Decrement : CounterAction()
    object Increment : CounterAction()
}

sealed class CounterEffect {
    object ShowOverflow : CounterEffect()
}
```

2. Implement a reducer:
```kotlin
class CounterReducer : Reducer<CounterState, CounterAction, CounterEffect> {
    
    override fun reduce(state: CounterState, action: CounterAction): Next<CounterState, CounterEffect> =
        when (action) {
        
            CounterAction.Decrement -> {
                if (state.count > 0) {
                    next(state.copy(count = state.count - 1))
                } else {
                    nothing()
                }
            }
            
            CounterAction.Increment -> {
                if (state.count < MAX_COUNT) {
                    next(state.copy(count = state.count + 1))
                } else {
                    effects(CounterEffect.ShowOverflow)
                }
            }
        }
}
```

3. Implement an effect handler:
```kotlin
class CounterEffectHandler(
   private val showMessage: (String) -> Unit
) : EffectHandler<CounterEffect, CounterAction> {
    
    override suspend fun handleEffect(effect: CounterEffect, actionConsumer: (CounterAction) -> Unit) {
        when (effect) {
            is CounterEffect.ShowOverflow -> showMessage("Overflow!")
        }
    }
}
```

4. Connect all together in Loop:
```kotlin
fun CounterLoop(showMessage: (String) -> Unit) = Loop(
    initialState = CounterState(0),
    reducer = CounterReducer(),
    effectHandlers = listOf(CounterEffectHandler(showMessage))
)
```

5. Use the loop in View Model:
```kotlin
class CounterViewModel : ViewModel() {

    private val counterLoop = CounterLoop(
        showMessage = { Log.d("Counter", it) }
    )

    val counterState: StateFlow<CounterState> get() = counterLoop.stateFlow

    init {
        counterLoop.startIn(viewModelScope)
    }

    fun onMinusButtonClicked() {
        counterLoop.dispatch(CounterAction.Decrement)
    }

    fun onPlusButtonClicked() {
        counterLoop.dispatch(CounterAction.Increment)
    }
}
```

## When to use Loop?
The sample above shows that quite a lot of code is required to implement a simple counter with Sesame Loop. But don't rush to conclusion. Loop shines when a complex state management appears. It is much simpler to write, read and debug a non-trivial code such as [paged data loading](https://github.com/aartikov/Sesame/blob/master/loading/src/main/kotlin/me/aartikov/sesame/loading/paged/internal/PagedLoadingLoop.kt) using Loop.