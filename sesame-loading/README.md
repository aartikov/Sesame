# Loading
Helps to manage state for data loading.

**Note**: there is an alternative library of the same author called [Replica](https://github.com/aartikov/Replica).

## How to use?

### Ordinary loading
For ordinary loading (without pagination) a state looks like that:

```kotlin
sealed class State<out T> {
    object Empty : State<Nothing>()
    object Loading : State<Nothing>()
    data class Error(val throwable: Throwable) : State<Nothing>()
    data class Data<T>(val data: T, val refreshing: Boolean) : State<T>()
}
```

`OrdinaryLoading` manages loading state. To create it specify a suspend function that loads data. Use it in View Model:

```kotlin
interface ProfileRepository {

    suspend fun loadProfile(): Profile
}
```

```kotlin
class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    private val profileLoading = OrdinaryLoading(
        viewModelScope,
        load = { profileGateway.loadProfile() }
    )
    
    val profileState = profileLoading.stateFlow

    init {
        profileLoading.refresh()
    }

    fun onPullToRefresh() {
        profileLoading.refresh()
    }
}
```

### Flow loading
A state for `FlowLoading` is the same as for `OrdinaryLoading`. But `FlowLoading` allows to add a cache (an observable data source):

```
interface ProfileRepository {

    suspend fun loadProfile(): Profile

    fun observeProfile(): Flow<Profile?>
}
```

```kotlin
class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    private val profileLoading = FlowLoading(
        viewModelScope,
        load = { profileGateway.loadProfile() },
        observe = { profileGateway.observeProfile() },
    )

    val profileState = profileLoading.stateFlow
    
    init {
        profileLoading.refresh()
    }

    fun onPullToRefresh() {
        profileLoading.refresh()
    }
}
```

### Paged loading
A paged loading state is more complex:
```kotlin
sealed class State<out T> {
    object Empty : State<Nothing>()
    object Loading : State<Nothing>()
    data class Error(val throwable: Throwable) : State<Nothing>()
    data class Data<T>(val data: List<T>, val status: DataStatus) : State<T>()
}

enum class DataStatus {
    Normal,
    Refreshing,
    LoadingMore,
    FullData
}
```

To create `PagedLoading` specify how to load pages. Use it in View Model:

```kotlin
interface MoviesRepository {

    suspend fun loadMovies(offset: Int, limit: Int): List<Movie>
}

class MoviesViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {
    
    companion object {
        private const val PAGE_SIZE = 30
    }
    
    private val moviesLoading = PagedLoading<Movie>(
        viewModelScope,
        loadPage = { 
            val movies = moviesRepository.loadMovies(it.loadedData.size, PAGE_SIZE)
            Page(movies, hasNextPage = movies.size >= PAGE_SIZE)
        }
    )

    val moviesState = moviesLoading.stateFlow

    init {
        moviesLoading.refresh()
    }

    fun onPullToRefresh() {
        moviesLoading.refresh()
    }

    fun onLoadMore() {
        moviesLoading.loadMore()
    }
}
```

### Error handling
Loading shows an error as `State.Error` if there is no previously loaded data. If refreshing fails when some data has already been loaded an error dialog should be shown on top. To do it use `handleErrors` method:

```kotlin
    init {
        profileLoading.handleErrors(viewModelScope) { error ->
            if (error.hasData) {
                showErrorDialog(error.throwable)
            }
        }
    }
```
