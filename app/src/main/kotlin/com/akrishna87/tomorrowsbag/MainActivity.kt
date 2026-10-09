package com.akrishna87.tomorrowsbag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akrishna87.tomorrowsbag.ui.TomorrowsBagViewModel
import com.akrishna87.tomorrowsbag.ui.TomorrowsBagViewModelFactory
import com.akrishna87.tomorrowsbag.ui.navigation.TomorrowsBagNavHost
import com.akrishna87.tomorrowsbag.ui.theme.TomorrowsBagTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TomorrowsBagApp
        val factory = TomorrowsBagViewModelFactory(app.repository, app.profilePrefs)

        setContent {
            TomorrowsBagTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Created once here (not inside a per-screen composable) so
                    // switching between the Kid and Parent tabs never loses the
                    // in-progress draft selection or re-queries the database.
                    val viewModel: TomorrowsBagViewModel = viewModel(factory = factory)
                    TomorrowsBagNavHost(viewModel)
                }
            }
        }
    }
}
