package me.aartikov.sesamecomposesample.features.profile.data

import me.aartikov.sesamecomposesample.features.profile.domain.Profile

interface ProfileGateway {

    suspend fun loadProfile(): Profile
}