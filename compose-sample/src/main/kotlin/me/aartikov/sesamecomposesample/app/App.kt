package me.aartikov.sesamecomposesample.app

import android.app.Application
import me.aartikov.sesamecomposesample.core.ComponentFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.dsl.koinApplication

class App : Application() {

    lateinit var koin: Koin
        private set

    override fun onCreate() {
        super.onCreate()

        koin = createKoin().also {
            it.declare(ComponentFactory(it))
        }
    }

    private fun createKoin(): Koin {
        return koinApplication {
            androidContext(this@App)
            modules(allModules)
        }.koin
    }
}

val Application.koin get() = (this as App).koin