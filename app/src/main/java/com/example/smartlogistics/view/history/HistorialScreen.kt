package com.example.smartlogistics.view.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartlogistics.R
import com.example.smartlogistics.model.LecturaEntity
import com.example.smartlogistics.viewmodel.LecturaViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun HistorialScreen(
    viewmodel: LecturaViewModel,
    modifier: Modifier = Modifier,
) {
    val lecturas by viewmodel.historial.collectAsStateWithLifecycle(initialValue = emptyList())

    if (lecturas.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
        ) {
            Text(
                text = stringResource(R.string.historial_titulo),
                style = MaterialTheme.typography.headlineSmall,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.historial_vacio),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.historial_titulo),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            items(lecturas, key = { it.id }) { lectura ->
                LecturaCard(lectura)
            }
        }
    }
}

@Composable
private fun LecturaCard(lectura: LecturaEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.temperatura_historial, lectura.temperatura),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(stringResource(R.string.latitud_historial, lectura.latitud))
            Text(stringResource(R.string.longitud_historial, lectura.longitud))
            Text(
                text = stringResource(
                    R.string.fecha_hora_historial,
                    DateFormat.getDateTimeInstance(
                        DateFormat.SHORT,
                        DateFormat.MEDIUM,
                    ).format(Date(lectura.fechaHora)),
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
