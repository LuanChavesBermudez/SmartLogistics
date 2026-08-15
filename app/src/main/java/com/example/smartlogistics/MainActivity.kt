package com.example.smartlogistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.smartlogistics.view.navigation.AppDestinations
import com.example.smartlogistics.view.history.HistorialScreen
import com.example.smartlogistics.view.temperature.RegistrarScreen
import com.example.smartlogistics.view.theme.SmartLogisticsTheme
import com.example.smartlogistics.viewmodel.LecturaViewModel

class MainActivity : ComponentActivity() {
    private val viewmodel: LecturaViewModel by viewModels {
        LecturaViewModel.Factory(
            (application as DatabaseInstance).db.lecturaDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SmartLogisticsTheme {
                SmartLogisticsApp(viewmodel)
            }
        }
    }
}

@Composable
fun SmartLogisticsApp(viewmodel: LecturaViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.REGISTRAR) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (currentDestination) {
                AppDestinations.REGISTRAR -> RegistrarScreen(viewmodel, modifier)
                AppDestinations.HISTORIAL -> HistorialScreen(viewmodel, modifier)
            }
        }
    }
}
