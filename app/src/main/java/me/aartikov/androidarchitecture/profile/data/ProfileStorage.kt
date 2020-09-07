package me.aartikov.androidarchitecture.profile.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.aartikov.androidarchitecture.profile.domain.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileStorage @Inject constructor() {

    private val profileStateFlow = MutableStateFlow<Profile?>(null)

    suspend fun saveProfile(profile: Profile) {
        profileStateFlow.value = profile
    }

    fun getProfile(): Flow<Profile?> {
        return profileStateFlow
    }

    suspend fun removeProfile() {
        profileStateFlow.value = null
    }
}