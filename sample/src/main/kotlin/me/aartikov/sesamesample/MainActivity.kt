package me.aartikov.sesamesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageDispatcher
import me.aartikov.sesame.navigation.NavigationMessageHandler
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.counter.CounterFragment
import me.aartikov.sesamesample.dialogs.ClockFragment
import me.aartikov.sesamesample.dialogs.DialogsFragment
import me.aartikov.sesamesample.menu.MenuFragment
import me.aartikov.sesamesample.movies.ui.MoviesFragment
import me.aartikov.sesamesample.profile.ui.ProfileFragment
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
            navigator.setRoot(MenuFragment())
        }
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is Back -> {
                val success = navigator.back()
                if (!success) finish()
            }
            is OpenCounterScreen -> navigator.goTo(CounterFragment())
            is OpenProfileScreen -> navigator.goTo(ProfileFragment())
            is OpenDialogsScreen -> navigator.goTo(DialogsFragment())
            is OpenMoviesScreen -> navigator.goTo(MoviesFragment())
            is OpenClockScreen -> navigator.goTo(ClockFragment())
        }
        return true
    }

    override fun onBackPressed() {
        (navigator.currentScreen as? BaseFragment<*>)?.onBackPressed()
    }
}