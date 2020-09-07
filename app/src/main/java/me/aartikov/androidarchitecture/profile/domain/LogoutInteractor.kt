package me.aartikov.androidarchitecture.profile.domain

import me.aartikov.androidarchitecture.profile.data.ProfileStorage
import javax.inject.Inject

class LogoutInteractor @Inject constructor(
    private val profileStorage: ProfileStorage
) {

    suspend fun execute() {
        profileStorage.removeProfile()
    }
}