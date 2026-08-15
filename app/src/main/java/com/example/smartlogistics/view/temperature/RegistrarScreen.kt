package com.example.smartlogistics.view.temperature

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.app.ActivityCompat
import com.example.smartlogistics.R
import com.example.smartlogistics.viewmodel.LecturaViewModel

@Composable
fun RegistrarScreen(
    viewmodel: LecturaViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewmodel.registroUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    val permissionPreferences = context.getSharedPreferences(
        "location_permission_preferences",
        Context.MODE_PRIVATE,
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val ubicacionPermitida = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (ubicacionPermitida) {
            viewmodel.obtenerUbicacion()
        } else {
            val puedeSolicitarDeNuevo = activity != null && (
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
            viewmodel.informarPermisoUbicacionDenegado(puedeSolicitarDeNuevo)
        }
    }

    fun solicitarUbicacion() {
        val ubicacionPermitida = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (ubicacionPermitida) {
            viewmodel.obtenerUbicacion()
        } else if (permissionPreferences.getBoolean("requested", false)) {
            val puedeSolicitarDeNuevo = activity != null && (
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
            viewmodel.informarPermisoUbicacionDenegado(puedeSolicitarDeNuevo)
        } else {
            permissionPreferences.edit { putBoolean("requested", true) }
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.registrar_titulo),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.registrar_instruccion),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = uiState.temperatura,
            onValueChange = viewmodel::actualizarTemperatura,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.temperatura_etiqueta)) },
            suffix = { Text(stringResource(R.string.grados_celsius)) },
            singleLine = true,
            isError = uiState.temperaturaError != null,
            supportingText = uiState.temperaturaError?.let { error ->
                { Text(error) }
            },
            enabled = !uiState.obteniendoUbicacion,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (viewmodel.validarTemperatura() != null) {
                    solicitarUbicacion()
                }
            },
            enabled = !uiState.obteniendoUbicacion,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.obteniendoUbicacion) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
                Text(
                    text = stringResource(R.string.obteniendo_ubicacion),
                    modifier = Modifier.padding(start = 8.dp),
                )
            } else {
                Text(text = stringResource(R.string.registrar_lectura))
            }
        }
        uiState.ubicacionError?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            when {
                uiState.puedeSolicitarPermiso -> Button(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                    },
                ) {
                    Text(stringResource(R.string.conceder_permiso))
                }

                uiState.mostrarAjustesAplicacion -> Button(
                    onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null),
                            )
                        )
                    },
                ) {
                    Text(stringResource(R.string.abrir_ajustes_aplicacion))
                }

                uiState.mostrarAjustesUbicacion -> Button(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    },
                ) {
                    Text(stringResource(R.string.activar_ubicacion))
                }
            }
        }
        uiState.registroMensaje?.let { mensaje ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensaje,
                color = if (uiState.registroExitoso) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        uiState.coordenadas?.let { coordenadas ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(
                    R.string.coordenadas_obtenidas,
                    coordenadas.latitud,
                    coordenadas.longitud,
                ),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
