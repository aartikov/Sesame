package me.aartikov.sesamecomposesample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import me.aartikov.sesamecomposesample.di.*
import me.aartikov.sesamecomposesample.root.RootUi
import me.aartikov.sesamecomposesample.theme.AppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val koinApp = koinApplication {
            androidContext(this@MainActivity)
            modules(getAllModules())
        }

        ComponentFactoryModule.koin = koinApp.koin

        val rootComponent = koinApp.koin.get<ComponentFactory>().createRootComponent(
            defaultComponentContext()
        )

        setContent {
            AppTheme {
                RootUi(rootComponent)
            }
        }
    }

    private fun getAllModules(): List<Module> = listOf(
        GatewayModule.create(),
        InteractorModule.create(),
        ServiceModule.create(),
        ComponentFactoryModule.create()
    )
}