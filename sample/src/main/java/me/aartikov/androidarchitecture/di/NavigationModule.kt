package me.aartikov.androidarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.aartikov.lib.navigation.NavigationMessageDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Singleton
    @Provides
    fun provideNavigationMessageDispatcher(): NavigationMessageDispatcher {
        return NavigationMessageDispatcher()
    }
}