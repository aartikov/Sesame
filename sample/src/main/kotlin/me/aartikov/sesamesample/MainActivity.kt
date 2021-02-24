package me.aartikov.sesamesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesamesample.counter.CounterScreen
import me.aartikov.sesamesample.dialogs.ClockScreen
import me.aartikov.sesamesample.dialogs.DialogsScreen
import me.aartikov.sesamesample.menu.MenuScreen
import me.aartikov.sesamesample.movies.ui.MoviesScreen
import me.aartikov.sesamesample.profile.ui.ProfileScreen
import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageDispatcher
import me.aartikov.sesame.navigation.NavigationMessageHandler
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationMessageHandler {

    @Inject
    internal lateinit var navigationMessageDispatcher: NavigationMessageDispatcher

    private lateinit var navigator: FragmentNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationMessageDispatcher.attach(this)
        navigator = FragmentNavigator(R.id.container, supportFragmentManager)

        if (savedInstanceState == null) {
            navigator.setRoot(MenuScreen())
        }
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is Back -> navigator.back()
            is OpenCounterScreen -> navigator.goTo(CounterScreen())
            is OpenProfileScreen -> navigator.goTo(ProfileScreen())
            is OpenDialogsScreen -> navigator.goTo(DialogsScreen())
            is OpenMoviesScreen -> navigator.goTo(MoviesScreen())
            is OpenClockScreen -> navigator.goTo(ClockScreen())
        }
        return true
    }

    override fun onBackPressed() {
        if (!navigator.back()) finish()
    }
}