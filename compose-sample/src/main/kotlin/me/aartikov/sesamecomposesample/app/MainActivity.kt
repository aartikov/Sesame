package me.aartikov.sesamecomposesample.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.core.theme.AppTheme
import me.aartikov.sesamecomposesample.features.root.createRootComponent
import me.aartikov.sesamecomposesample.features.root.ui.RootUi

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val componentFactory = application.koin.get<ComponentFactory>()
        val rootComponent = componentFactory.createRootComponent(defaultComponentContext())

        setContent {
            AppTheme {
                RootUi(rootComponent)
            }
        }
    }
}