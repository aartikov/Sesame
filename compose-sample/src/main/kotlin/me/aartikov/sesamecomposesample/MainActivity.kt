package me.aartikov.sesamecomposesample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import me.aartikov.sesamecomposesample.root.RealRootComponent
import me.aartikov.sesamecomposesample.root.RootUi
import me.aartikov.sesamecomposesample.theme.AppTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponent = RealRootComponent(defaultComponentContext())
        setContent {
            AppTheme {
                RootUi(rootComponent)
            }
        }
    }
}