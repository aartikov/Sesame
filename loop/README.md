# Loop

Provides a simple MVI implementation.

## Core concepts of MVI
The basic principle of any MVI implementation is how to manage a **state**. A state in MVI is immutable. The only way to change a state is to send an **action**. A new state is producer by **reducer** - a pure function with a signature `fun reduce(state: StateT, action: ActionT): StateT`.  
But the most interesting part is how **side effects** are handled. Here implementations can vary. Just to remind you, side effects are operations other than state reducing: network calls, database queries, etc. Sesame Loop provides a consise way to handle side effects.

## How does Loop handle side effects?
Loop extends the concept of reducer:

```kotlin
interface Reducer<StateT, ActionT, EffectT> {

    fun reduce(state: StateT, action: ActionT): Next<StateT, EffectT>
}
```

Instead of just `StateT` this reducer returns `Next`:
```
data class Next<StateT, EffectT>(
   val state: StateT?, 
   val effects: List<EffectT>
)
```

`Next` contains a new state (null means "no changes") and a list of side effects. A side effect does not executes operation by itself, it is just a description (data class) which operation has to be executed. So `reduce` is still a pure function.

To execute side effects `EffectHandler` should be implemented:

```
interface EffectHandler<in EffectT, out ActionT> {
    suspend fun handleEffect(effect: EffectT, actionConsumer: (ActionT) -> Unit)
}
```

An implementation can emit additional actions with `actionConsumer` callback. For example, to report a result of operation.

## How to use?

1. Declare state, actions and effects.

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
The sample above shows that quite a lot of code has to be written to implement a simple counter with Sesame Loop. But don't rush to conclusion. Loop shines when a complex state management is required. It is much simpler to write, read and debug a non-trivial code such as [paged data loading](https://github.com/aartikov/Sesame/blob/readme/loading/src/main/kotlin/me/aartikov/sesame/loading/paged/internal/PagedLoadingLoop.kt) using Loop.