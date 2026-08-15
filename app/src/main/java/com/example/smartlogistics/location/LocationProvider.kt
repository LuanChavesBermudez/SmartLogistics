package com.example.smartlogistics.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

data class Coordenadas(
    val latitud: Double,
    val longitud: Double,
)

interface LocationProvider {
    suspend fun obtenerUbicacionActual(): Coordenadas?
}

class LocationDisabledException : IllegalStateException()

class FusedLocationProvider(context: Context) : LocationProvider {
    private val client = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override suspend fun obtenerUbicacionActual(): Coordenadas? {
        val locationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        if (!locationEnabled) throw LocationDisabledException()

        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(5_000)
                .setDurationMillis(10_000)
                .build()

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }

            client.getCurrentLocation(request, cancellationTokenSource.token)
                .addOnSuccessListener { location: Location? ->
                    if (continuation.isActive) {
                        continuation.resume(
                            location?.let { Coordenadas(it.latitude, it.longitude) }
                        )
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
        }
    }
}
