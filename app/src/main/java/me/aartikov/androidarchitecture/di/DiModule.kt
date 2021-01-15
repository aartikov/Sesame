package me.aartikov.androidarchitecture.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.aartikov.lib.navigation.NavigationMessageDispatcher
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DiModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideNavigationMessageDispatcher(): NavigationMessageDispatcher {     // TODO: must be in ActivityComponent
        return NavigationMessageDispatcher()
    }
}