package info.androidhive.androidbacknavigation

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import info.androidhive.androidbacknavigation.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showBackToast()
        }
    }


    /**
     * Listening to navigation destination and enable back press callback only on home screen
     * */
    private val navControllerListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            // enable back press callback when destination is home fragment
            backPressCallback.isEnabled = destination.id == R.id.FirstFragment
        }

    /**
     * Shows toast and disables back press callback. If user presses back again with in 1sec,
     * back navigation will happen otherwise back press callback will be enabled again
     * */
    @OptIn(DelicateCoroutinesApi::class)
    private fun showBackToast() {
        Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show()
        backPressCallback.isEnabled = false

        GlobalScope.launch {
            delay(1000)
            // user hasn't pressed back within 1 sec. Enable back press callback again
            backPressCallback.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        onBackPressedDispatcher.addCallback(backPressCallback)
    }

    override fun onStop() {
        super.onStop()
        backPressCallback.remove()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(navControllerListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(navControllerListener)
    }
}