package me.aartikov.sesame.navigation

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * Helps to send [navigation messages][NavigationMessage] from a View Model to [NavigationMessageDispatcher].
 */
class NavigationMessageQueue {
    private val channel = Channel<NavigationMessage>(Channel.UNLIMITED)
    val flow = channel.receiveAsFlow()

    /**
     * Sends navigation message. If message can't be sent immediately it will be queued.
     * @see bind for more details.
     */
    fun send(message: NavigationMessage) {
        channel.offer(message)
    }
}

/**
 * Binds a queue to [NavigationMessageDispatcher]. Navigation messages sent to a bound queue will be passed to a dispatcher automatically.
 * Message will be dispatched only when [lifecycleOwner] is in resumed state and [dispatcher] can handle messages (Activity is in resumed state and no other message handling is in progress).
 *
 * @param node a node from which message dispatching will be started (see: [NavigationMessageDispatcher.dispatch]). In most cases it should be Fragment/Activity that owns a View Model with the queue.
 * @param lifecycleOwner to provide a lifecycle of Fragment/Activity that owns a View Model with the queue.
 */
fun NavigationMessageQueue.bind(
    dispatcher: NavigationMessageDispatcher,
    node: Any,
    lifecycleOwner: LifecycleOwner
) {
    val lifecycleOwnerResumed = resumedAsFlow(lifecycleOwner.lifecycle)

    val pauseMessageDispatching = combine(
        lifecycleOwnerResumed, dispatcher.canHandleMessages
    ) { resumed, dispatcherCanHandle ->
        !resumed || !dispatcherCanHandle
    }

    flow.pauseWhenRequired(pauseMessageDispatching, lifecycleOwner.lifecycleScope)
        .onEach { message ->
            dispatcher.dispatch(message, node)
        }
        .launchIn(lifecycleOwner.lifecycleScope)
}

// Creates flow that indicates if a lifecycle is in resumed state.
private fun resumedAsFlow(lifecycle: Lifecycle): Flow<Boolean> {
    val resumed = MutableStateFlow(lifecycle.currentState == Lifecycle.State.RESUMED)

    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            resumed.value = true
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            resumed.value = false
        }
    })

    return resumed
}

// Starts emit items when pauseRequired returned false, stops emit items when pauseRequired returned true.
fun <T> Flow<T>.pauseWhenRequired(pauseRequired: Flow<Boolean>, scope: CoroutineScope): Flow<T> {
    val flow = MutableSharedFlow<T>()
    var job: Job? = null

    pauseRequired
        .onEach { pause ->
            if (pause) {
                job?.cancel()
                job = null
            } else {
                job = this
                    .onEach { flow.emit(it) }
                    .launchIn(scope)
            }
        }
        .launchIn(scope)

    return flow
}