package com.example.tratohecho.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.tratohecho.data.Cliente
import com.google.android.gms.location.LocationServices
import java.io.File

@Composable
fun VisitaScreen(
    cliente: Cliente?,
    onGuardarVisita: (String, Double?, Double?) -> Unit
) {
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }
    var mensaje by remember { mutableStateOf("") }

    val tomarFotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { resultado ->
        mensaje = if (resultado) "Fotografía capturada correctamente" else "Captura cancelada"
    }

    fun abrirCamara() {
        val carpeta = File(context.cacheDir, "images").apply { mkdirs() }
        val archivo = File.createTempFile("visita_", ".jpg", carpeta)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivo
        )
        fotoUri = uri
        tomarFotoLauncher.launch(uri)
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { aceptado ->
        if (aceptado) abrirCamara() else mensaje = "Permiso de cámara denegado"
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacion() {
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                    mensaje = "Ubicación obtenida correctamente"
                } else {
                    mensaje = "No hay ubicación reciente. Activa GPS y vuelve a intentar."
                }
            }
            .addOnFailureListener {
                mensaje = "No se pudo obtener la ubicación"
            }
    }

    val permisoUbicacionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos.values.any { it }) obtenerUbicacion() else mensaje = "Permiso de ubicación denegado"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Visita en terreno", style = MaterialTheme.typography.headlineMedium)
        Text(cliente?.nombre ?: "Cliente no encontrado")
        Text("Esta acción es opcional: sirve para respaldar una visita comercial con cámara y GPS.")
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (tienePermiso) abrirCamara() else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Text("  Tomar fotografía")
        }

        OutlinedButton(
            onClick = {
                val tieneGps = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (tieneGps) {
                    obtenerUbicacion()
                } else {
                    permisoUbicacionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null)
            Text("  Obtener GPS")
        }

        Spacer(Modifier.height(12.dp))

        fotoUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Foto de la visita",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        if (latitud != null && longitud != null) {
            Text("Latitud: $latitud")
            Text("Longitud: $longitud")
        }

        AnimatedVisibility(visible = mensaje.isNotBlank()) {
            Text(mensaje, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                onGuardarVisita(fotoUri?.toString().orEmpty(), latitud, longitud)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Text("  Guardar visita")
        }
    }
}
