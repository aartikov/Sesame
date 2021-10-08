package me.aartikov.sesamecomposesample.di

import me.aartikov.sesamecomposesample.movies.domain.LoadMoviesPageInteractor
import org.koin.dsl.module

object InteractorModule {

    fun create() = module {

        factory { LoadMoviesPageInteractor(get()) }
    }
}