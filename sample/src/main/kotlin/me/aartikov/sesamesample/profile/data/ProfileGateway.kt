package me.aartikov.sesamesample.profile.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.aartikov.sesamesample.profile.domain.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileGateway @Inject constructor() {

    companion object {
        private const val DEFAULT_CACHE_EXPIRATION_TIME = 5 * 60   // seconds
    }

    private val cache = SingleItemCache<Profile>(DEFAULT_CACHE_EXPIRATION_TIME)

    private var counter = 0

    suspend fun loadProfile(fresh: Boolean): Profile {
        return cache.request(fresh) {
            loadProfileInternal()
        }
    }

    private suspend fun loadProfileInternal(): Profile = withContext(Dispatchers.IO) {
        delay(1000)
        val success = counter % 2 == 1
        counter++

        if (success) {
            Profile(
                name = "John Smith",
                avatarUrl = "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cg_face%2Cq_auto:good%2Cw_300/MTIwNjA4NjMzOTc0MTk1NzI0/john-smith-9486928-1-402.jpg"
            )
        } else {
            throw RuntimeException("Emulated failure. Please, try again.")
        }
    }

    suspend fun clearCache() {
        cache.clear()
    }
}