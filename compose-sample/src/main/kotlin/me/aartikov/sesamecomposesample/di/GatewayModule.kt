package me.aartikov.sesamecomposesample.di

import me.aartikov.sesamecomposesample.movies.data.MoviesGateway
import me.aartikov.sesamecomposesample.movies.data.MoviesGatewayImpl
import me.aartikov.sesamecomposesample.profile.data.ProfileGateway
import me.aartikov.sesamecomposesample.profile.data.ProfileGatewayImpl
import org.koin.dsl.module

object GatewayModule {

    fun create() = module {

        single<ProfileGateway> { ProfileGatewayImpl() }
        single<MoviesGateway> { MoviesGatewayImpl() }
    }
}