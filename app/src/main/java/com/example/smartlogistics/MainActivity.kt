package com.example.smartlogistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartlogistics.view.navigation.AppDestinations
import com.example.smartlogistics.view.navigation.MainDestination
import com.example.smartlogistics.view.history.HistorialScreen
import com.example.smartlogistics.view.temperature.RegistrarScreen
import com.example.smartlogistics.view.theme.SmartLogisticsTheme
import com.example.smartlogistics.location.FusedLocationProvider
import com.example.smartlogistics.viewmodel.LecturaViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewmodel: LecturaViewModel by viewModels {
        LecturaViewModel.Factory(
            dao = (application as DatabaseInstance).db.lecturaDao(),
            locationProvider = FusedLocationProvider(applicationContext),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartLogisticsApp(viewmodel: LecturaViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(MainDestination.INICIO) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .padding(vertical = 16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    MainDestination.entries.forEach { destination ->
                        NavigationDrawerItem(
                            label = { Text(stringResource(destination.label)) },
                            selected = destination == currentDestination,
                            onClick = {
                                currentDestination = destination
                                scope.launch { drawerState.close() }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(destination.icon),
                                    contentDescription = null,
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(currentDestination.label)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = stringResource(R.string.abrir_menu),
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            when (currentDestination) {
                MainDestination.INICIO -> HomeScreen(Modifier.padding(innerPadding))
                MainDestination.REGISTRO -> RegistroSection(
                    viewmodel = viewmodel,
                    modifier = Modifier.padding(innerPadding),
                )
                MainDestination.MAPA -> PendingFeatureScreen(
                    title = stringResource(R.string.menu_mapa),
                    message = stringResource(R.string.mapa_pendiente),
                    icon = R.drawable.ic_map,
                    modifier = Modifier.padding(innerPadding),
                )
                MainDestination.SPOTIFY -> PendingFeatureScreen(
                    title = stringResource(R.string.menu_spotify),
                    message = stringResource(R.string.spotify_pendiente),
                    icon = R.drawable.ic_music,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun RegistroSection(
    viewmodel: LecturaViewModel,
    modifier: Modifier = Modifier,
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.REGISTRAR) }

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            painter = painterResource(destination.icon),
                            contentDescription = stringResource(destination.label),
                        )
                    },
                    label = { Text(stringResource(destination.label)) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination },
                )
            }
        },
    ) {
        when (currentDestination) {
            AppDestinations.REGISTRAR -> RegistrarScreen(viewmodel)
            AppDestinations.HISTORIAL -> HistorialScreen(viewmodel)
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF087F8C), Color(0xFF064E59)),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.28f),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.descripcion_caja_producto),
                modifier = Modifier.size(220.dp),
            )
            Text(
                text = stringResource(R.string.bienvenida_titulo),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.bienvenida_descripcion),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PendingFeatureScreen(
    title: String,
    message: String,
    icon: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            modifier = Modifier.padding(top = 20.dp),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}
