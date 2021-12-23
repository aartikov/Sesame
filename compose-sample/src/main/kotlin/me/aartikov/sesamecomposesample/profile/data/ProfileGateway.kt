package me.aartikov.sesamecomposesample.profile.data

import me.aartikov.sesamecomposesample.profile.domain.Profile

interface ProfileGateway {

    suspend fun loadProfile(): Profile
}