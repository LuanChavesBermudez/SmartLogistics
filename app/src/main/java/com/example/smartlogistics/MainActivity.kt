package com.example.smartlogistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.smartlogistics.ui.theme.SmartLogisticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartLogisticsTheme {
                SmartLogisticsApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun SmartLogisticsApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TEMPERATURA) }

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
                AppDestinations.TEMPERATURA -> TemperaturaScreen(modifier)
                AppDestinations.HISTORIAL -> HistorialScreen(modifier)
            }
        }
    }
}

@Composable
fun TemperaturaScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { /* TODO: Implementar acción */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "TEMPERATURA ACTUAL")
        }
    }
}

@Composable
fun HistorialScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pantalla de Historial")

    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    TEMPERATURA("Temperatura", R.drawable.ic_home),
    HISTORIAL("Historial", R.drawable.ic_favorite),
}

@Preview(showBackground = true)
@Composable
fun TemperaturaScreenPreview() {
    SmartLogisticsTheme {
        TemperaturaScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun SmartLogisticsAppPreview() {
    SmartLogisticsTheme {
        SmartLogisticsApp()
    }
}