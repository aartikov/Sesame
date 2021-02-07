package me.aartikov.androidarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import me.aartikov.lib.navigation.AndroidNavigationMessageDispatcher
import me.aartikov.lib.navigation.NavigationMessageDispatcher

@Module
@InstallIn(ActivityComponent::class)
object NavigationModule {

    @ActivityScoped
    @Provides
    fun provideNavigationMessageDispatcher(): NavigationMessageDispatcher {
        return AndroidNavigationMessageDispatcher()
    }
}