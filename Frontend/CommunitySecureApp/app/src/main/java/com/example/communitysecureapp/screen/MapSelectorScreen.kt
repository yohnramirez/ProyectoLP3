package com.example.communitysecureapp.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectorScreen(
    navController: NavController,
    initialLocation: GeoPoint?
) {

    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var selectedGeoPointState by remember { mutableStateOf<GeoPoint?>(initialLocation) }
    var currentMapCenter by remember {
        mutableStateOf(
            initialLocation ?: GeoPoint(
                4.7110,
                -74.0721
            )
        )
    }
    var currentMapZoom by remember { mutableDoubleStateOf(if (initialLocation != null) 16.0 else 12.0) }
    var isLoadingCurrentLocation by remember { mutableStateOf(false) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted && selectedGeoPointState == null) {
                fetchCurrentUserLocation(
                    fusedLocationClient, context,
                    onLoading = { isLoadingCurrentLocation = it },
                    onLocationReady = { geoPoint ->
                        selectedGeoPointState = geoPoint
                        currentMapCenter = geoPoint
                        currentMapZoom = 17.0
                    }
                )
            } else if (!isGranted) {
                errorMessage = "Permiso de ubicación denegado. Selecciona manualmente en el mapa."
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else if (initialLocation == null && selectedGeoPointState == null) {
            fetchCurrentUserLocation(
                fusedLocationClient, context,
                onLoading = { isLoadingCurrentLocation = it },
                onLocationReady = { geoPoint ->
                    selectedGeoPointState = geoPoint
                    currentMapCenter = geoPoint
                    currentMapZoom = 17.0
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setUseDataConnection(true)
        mapView.invalidate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Seleccionar Ubicación en Mapa",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        overlays.add(0, MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    selectedGeoPointState = it
                                    currentMapCenter = it
                                    errorMessage = null
                                }
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint?) = false
                        }))
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    if (view.zoomLevelDouble != currentMapZoom) {
                        view.controller.setZoom(currentMapZoom)
                    }
                    if (view.mapCenter.latitude != currentMapCenter.latitude ||
                        view.mapCenter.longitude != currentMapCenter.longitude
                    ) {
                        view.controller.setCenter(currentMapCenter)
                    }

                    view.overlays.removeAll { it is Marker && it.id == "selection_marker" }
                    selectedGeoPointState?.let { geoPoint ->
                        val marker = Marker(view).apply {
                            id = "selection_marker"
                            position = geoPoint
                            isDraggable = true
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            // icon = ContextCompat.getDrawable(context, R.drawable.ic_your_pin) // Tu icono personalizado
                            setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                                override fun onMarkerDrag(m: Marker) {}
                                override fun onMarkerDragEnd(m: Marker) {
                                    selectedGeoPointState = m.position
                                    currentMapCenter = m.position
                                }

                                override fun onMarkerDragStart(m: Marker) {}
                            })
                        }
                        view.overlays.add(marker)
                    }
                    view.invalidate()
                }
            )

            if (isLoadingCurrentLocation && selectedGeoPointState == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            selectedGeoPointState?.let {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Lat: ${"%.6f".format(it.latitude)}, Lon: ${"%.6f".format(it.longitude)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } ?: errorMessage?.let {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (!hasLocationPermission) {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    } else {
                        fetchCurrentUserLocation(
                            fusedLocationClient, context,
                            onLoading = { isLoadingCurrentLocation = it },
                            onLocationReady = { geoPoint ->
                                selectedGeoPointState = geoPoint
                                currentMapCenter = geoPoint
                                currentMapZoom = 17.0
                            }
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = if (isLoadingCurrentLocation) MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.12f
                )
                else MaterialTheme.colorScheme.primary,
                contentColor = if (isLoadingCurrentLocation) MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.38f
                )
                else MaterialTheme.colorScheme.onPrimary
            ) {
                if (isLoadingCurrentLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Centrar en mi ubicación")
                }
            }

            Button(
                onClick = {
                    selectedGeoPointState?.let { geoPoint ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_location",
                            geoPoint
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedGeoPointState != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = "Confirmar Ubicación",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchCurrentUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLoading: (Boolean) -> Unit,
    onLocationReady: (GeoPoint) -> Unit
) {
    onLoading(true)
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            onLoading(false)
            location?.let {
                onLocationReady(GeoPoint(it.latitude, it.longitude))
            } ?: run {
                Toast.makeText(
                    context,
                    "No se pudo obtener la ubicación actual.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .addOnFailureListener { exception ->
            onLoading(false)
            Toast.makeText(
                context,
                "Error al obtener ubicación: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}