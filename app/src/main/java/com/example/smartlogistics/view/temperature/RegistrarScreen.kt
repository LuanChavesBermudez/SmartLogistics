package com.example.smartlogistics.view.temperature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartlogistics.R
import com.example.smartlogistics.viewmodel.LecturaViewModel

@Composable
fun RegistrarScreen(
    viewmodel: LecturaViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewmodel.registroUiState.collectAsStateWithLifecycle()

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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewmodel.validarTemperatura() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.registrar_lectura))
        }
        if (uiState.temperaturaValida) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.temperatura_valida),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
