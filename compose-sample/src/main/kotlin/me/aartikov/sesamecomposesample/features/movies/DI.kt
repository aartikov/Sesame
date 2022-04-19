package me.aartikov.sesamecomposesample.features.movies

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.features.movies.data.MoviesGateway
import me.aartikov.sesamecomposesample.features.movies.data.MoviesGatewayImpl
import me.aartikov.sesamecomposesample.features.movies.domain.LoadMoviesPageInteractor
import me.aartikov.sesamecomposesample.features.movies.ui.MoviesComponent
import me.aartikov.sesamecomposesample.features.movies.ui.RealMoviesComponent
import org.koin.core.component.get
import org.koin.dsl.module

val moviesModule = module {
    single<MoviesGateway> { MoviesGatewayImpl() }
    factory { LoadMoviesPageInteractor(get()) }
}

fun ComponentFactory.createMoviesComponent(componentContext: ComponentContext): MoviesComponent {
    return RealMoviesComponent(componentContext, get(), get())
}
