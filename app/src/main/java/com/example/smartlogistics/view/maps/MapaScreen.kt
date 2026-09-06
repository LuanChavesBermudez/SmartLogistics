package com.example.smartlogistics.view.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.smartlogistics.location.FusedLocationProvider
import com.example.smartlogistics.location.LocationProvider
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapaScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val locationProvider: LocationProvider = remember {
        FusedLocationProvider(context)
    }

    val cameraPositionState = rememberCameraPositionState()
    val scope = rememberCoroutineScope()
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {
                obtenerUbicacion(
                    locationProvider,
                    cameraPositionState,
                    scope,
                )
            }
        }

    fun solicitarPermisos() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            obtenerUbicacion(
                locationProvider,
                cameraPositionState,
                scope,
            )
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        solicitarPermisos()
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = com.google.maps.android.compose.MapProperties(
            isMyLocationEnabled =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ) == PackageManager.PERMISSION_GRANTED
        ),
    )
}

private fun obtenerUbicacion(
    locationProvider: LocationProvider,
    cameraPositionState: com.google.maps.android.compose.CameraPositionState,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    scope.launch {
        val coordenadas = locationProvider.obtenerUbicacionActual()

        if (coordenadas != null) {
            val userLocation = LatLng(
                coordenadas.latitud,
                coordenadas.longitud,
            )

            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(
                    userLocation,
                    15f,
                )
        }
    }
}
