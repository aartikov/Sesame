package me.aartikov.sesamecomposesample

import android.app.Application
import me.aartikov.sesamecomposesample.di.GatewayModule
import me.aartikov.sesamecomposesample.di.InteractorModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(getAllModules())
        }
    }

    private fun getAllModules(): List<Module> = listOf(
        GatewayModule.create(),
        InteractorModule.create()
    )
}