package me.aartikov.sesamecomposesample.di

import me.aartikov.sesamecomposesample.profile.ui.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {

    fun create() = module {

        viewModel { ProfileViewModel(get()) }
    }
}