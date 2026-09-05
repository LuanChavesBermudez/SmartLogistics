package com.example.smartlogistics.view.navigation

import androidx.annotation.StringRes
import com.example.smartlogistics.R

enum class MainDestination(
    @param:StringRes val label: Int,
    val icon: Int,
) {
    INICIO(R.string.menu_inicio, R.drawable.ic_home),
    REGISTRO(R.string.menu_registro, R.drawable.ic_registrar),
    MAPA(R.string.menu_mapa, R.drawable.ic_map),
    SPOTIFY(R.string.menu_spotify, R.drawable.ic_music),
}

enum class AppDestinations(
    @param:StringRes val label: Int,
    val icon: Int,
) {
    REGISTRAR(R.string.menu_registrar_lectura, R.drawable.ic_registrar),
    HISTORIAL(R.string.menu_historial, R.drawable.ic_historial),
}
