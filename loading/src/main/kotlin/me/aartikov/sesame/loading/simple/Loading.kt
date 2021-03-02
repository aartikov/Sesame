package me.aartikov.sesame.loading.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

/**
 * Helps to load data and manage loading state.
 */
interface Loading<T : Any> {

    /**
     * Loading state.
     */
    sealed class State<out T> {
        /**
         * Empty data has been loaded or loading is not started yet. Data is empty if it is null or [isEmpty] returns true for it.
         */
        object Empty : State<Nothing>()

        /**
         * Loading is in progress and there is no previously loaded data.
         */
        object Loading : State<Nothing>()

        /**
         * Loading error has occurred and there is no previously loaded data.
         */
        data class Error(val throwable: Throwable) : State<Nothing>()

        /**
         * Non-empty data has been loaded. [refreshing] is true when second loading is in progress.
         */
        data class Data<T>(val data: T, val refreshing: Boolean = false) : State<T>()
    }

    /**
     * Loading event.
     */
    sealed class Event<out T> {
        /**
         * An error occurred. [stateDuringLoading] a state that was during the failed loading.
         */
        data class Error<T>(val throwable: Throwable, val stateDuringLoading: State<T>) : Event<T>() {

            /**
             * Is true when there is previously loaded data. It is useful to not show an error dialog when a fullscreen error is already shown.
             */
            val hasData get() = stateDuringLoading is State.Data
        }
    }

    /**
     * Flow of loading states.
     */
    val stateFlow: StateFlow<State<T>>

    /**
     * Flow of loading events.
     */
    val eventFlow: Flow<Event<T>>

    /**
     * Initializes a [Loading] object by providing a [CoroutineScope] to work in. Should be called once.
     */
    fun attach(scope: CoroutineScope): Job

    /**
     * Requests to load data.
     * @param fresh indicates that fresh data is required. See [OrdinaryLoader.load] and [FlowLoader.load]
     * @param reset if true than previously loaded data will be instantly dropped and in progress loading will be canceled.
     * If false than previously loaded data will be preserved until successful outcome and a loading request will be ignored if another one is already in progress.
     */
    fun load(fresh: Boolean, reset: Boolean = false)

    /**
     * Requests to cancel in progress loading.
     * @param reset if true than state will be reset to [Loading.State.Empty].
     */
    fun cancel(reset: Boolean = false)
}

/**
 * A shortcut for load(fresh = true, reset = false). Requests to load fresh data and preserve the old one until successful outcome.
 */
fun <T : Any> Loading<T>.refresh() = load(fresh = true, reset = false)

/**
 * A shortcut for load(fresh, reset = true). Requests to drop old data and load new one.
 * @param fresh indicates that fresh data is required. See [OrdinaryLoader.load] and [FlowLoader.load].
 */
fun <T : Any> Loading<T>.restart(fresh: Boolean = true) = load(fresh, reset = true)

/**
 * A shortcut for cancel(reset = true). Cancels loading and sets state to [Loading.State.Empty].
 */
fun <T : Any> Loading<T>.reset() = cancel(reset = true)

/**
 * Returns a current [Loading.State].
 */
val <T : Any> Loading<T>.state: Loading.State<T> get() = stateFlow.value

/**
 * Returns [Loading.State.Data.data] if it is available or null otherwise
 */
val <T : Any> Loading<T>.dataOrNull: T? get() = state.dataOrNull

/**
 * Returns [Loading.State.Error.throwable] if it is available or null otherwise
 */
val Loading<*>.errorOrNull: Throwable? get() = state.errorOrNull

/**
 * Returns [Loading.State.Data.data] if it is available or null otherwise
 */
val <T : Any> Loading.State<T>.dataOrNull: T? get() = (this as? Loading.State.Data)?.data

/**
 * Returns [Loading.State.Error.throwable] if it is available or null otherwise
 */
val Loading.State<*>.errorOrNull: Throwable? get() = (this as? Loading.State.Error)?.throwable

/**
 * A helper method to handle [Loading.Event.Error].
 */
fun <T : Any> Loading<T>.handleErrors(
    scope: CoroutineScope,
    handler: (Loading.Event.Error<T>) -> Unit
): Job {
    return eventFlow.filterIsInstance<Loading.Event.Error<T>>()
        .onEach {
            handler(it)
        }
        .launchIn(scope)
}
