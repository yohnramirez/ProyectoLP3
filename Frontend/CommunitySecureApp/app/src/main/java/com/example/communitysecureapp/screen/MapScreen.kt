package com.example.communitysecureapp.screen

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import android.Manifest
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.communitysecureapp.state.FormState
import com.example.communitysecureapp.viewmodel.FormReportViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, viewModel: FormReportViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val clientLocation = remember { LocationServices.getFusedLocationProviderClient(context) }
    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var hasLocationPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permissionRequired by rememberSaveable { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var userLocationMarkerState by remember { mutableStateOf<Marker?>(null) }
    var firstLocationObtained by remember { mutableStateOf(false) }
    var initialGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var formState by rememberSaveable(stateSaver = FormState.Saver) {
        mutableStateOf(
            FormState(
                incidentLocation = null,
                reportType = "",
                description = ""
            )
        )
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<GeoPoint?>(
            "selected_location",
            null
        )
            ?.collect { geoPoint ->
                if (geoPoint != null) {
                    formState = formState.copy(incidentLocation = geoPoint)
                    showBottomSheet = true
                    navController.currentBackStackEntry?.savedStateHandle?.remove<GeoPoint>("selected_location")
                }
            }
    }

    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            sheetState.show()
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && showBottomSheet) {
            showBottomSheet = false
        }
    }

    var locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { newLocation ->
                    val userGeoPoint = GeoPoint(newLocation.latitude, newLocation.longitude)

                    if (!firstLocationObtained) {
                        initialGeoPoint = userGeoPoint
                        firstLocationObtained = true
                    } else {
                        mapView.controller.setCenter(userGeoPoint)
                    }

                    userLocationMarkerState = updateOrCreateUserMarker(
                        mapView,
                        userGeoPoint,
                        userLocationMarkerState
                    )

                    isLoadingLocation = false
                    clientLocation.removeLocationUpdates(this)
                } ?: run {
                    Toast.makeText(
                        context,
                        "No se pudo obtener la ubicación actual",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    var locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            permissionRequired = true

            if (isGranted) {
                Toast.makeText(context, "Permiso de ubicacion concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permiso de ubicacion denegado", Toast.LENGTH_SHORT).show()
                isLoadingLocation = false
                firstLocationObtained = false
            }
        }
    )

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {

            if (!firstLocationObtained) {
                isLoadingLocation = true
            }

            try {

                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    clientLocation.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {

                                val userGeoPoint = GeoPoint(location.latitude, location.longitude)

                                if (!firstLocationObtained) {
                                    initialGeoPoint = userGeoPoint
                                    firstLocationObtained = true
                                } else {
                                    mapView.controller.setCenter(userGeoPoint)
                                }

                                userLocationMarkerState = updateOrCreateUserMarker(
                                    mapView,
                                    userGeoPoint,
                                    userLocationMarkerState
                                )

                                isLoadingLocation = false
                            } else {
                                if (!firstLocationObtained) isLoadingLocation = true
                                val locationRequest =
                                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 6000L)
                                        .setMinUpdateIntervalMillis(3000L)
                                        .setWaitForAccurateLocation(true)
                                        .build()
                                clientLocation.requestLocationUpdates(
                                    locationRequest,
                                    locationCallback,
                                    Looper.getMainLooper()
                                )
                            }
                        }
                        .addOnFailureListener { error ->
                            isLoadingLocation = false
                            Toast.makeText(
                                context, "Error al obtener ultima ubicacion: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    isLoadingLocation = false
                    hasLocationPermission = false
                }

            } catch (e: SecurityException) {
                isLoadingLocation = false
                Toast.makeText(context, "Excepcion de seguridad: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                hasLocationPermission = false
            }
        } else {
            isLoadingLocation = false
            firstLocationObtained = false
            initialGeoPoint = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasLocationPermission) {

            if (firstLocationObtained && initialGeoPoint != null) {

                AndroidView(
                    factory = { cxt ->
                        mapView.apply {
                            controller.setZoom(16.5)
                            controller.setCenter(initialGeoPoint)

                            userLocationMarkerState = updateOrCreateUserMarker(
                                this,
                                initialGeoPoint!!,
                                userLocationMarkerState
                            )
                            invalidate()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear Reporte",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else if (isLoadingLocation) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Obteniendo tu ubicacion inicial...")
                    }
                }
            } else {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No se pudo obtener la ubicacion para mostrar el mapa")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (hasLocationPermission) {
                                    firstLocationObtained = false
                                    isLoadingLocation = true

                                    val dummyActivityRecreationForRetry = mutableStateOf(false)
                                    dummyActivityRecreationForRetry.value =
                                        !dummyActivityRecreationForRetry.value
                                } else {
                                    permissionRequired = false
                                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }
                        ) {
                            Text(if (hasLocationPermission) "Reintentar Obtener Ubicación" else "Conceder Permiso")
                        }
                    }
                }
            }
        } else {

            LaunchedEffect(Unit) {
                if (!permissionRequired) {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (permissionRequired) {
                    Text("El permiso de ubicacion es necesario para visualizar el mapa.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }) {
                        Text("Conceder Permiso")
                    }
                } else {
                    Text("Solicitando permiso de ubicación...")
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator()
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                modifier = Modifier.fillMaxWidth()
            ) {
                FormReportScreen(
                    navController = navController,
                    formState = formState,
                    onFormStateChange = { newState ->
                        formState = newState
                    },
                    onCloseSheet = { showBottomSheet = false },
                    onSubmit = { geoPoint, reportType, description ->
                        formState = formState.copy(
                            incidentLocation = geoPoint,
                            reportType = reportType,
                            description = description
                        )

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Reporte enviado: $reportType")
                        }
                        showBottomSheet = false
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            clientLocation.removeLocationUpdates(locationCallback)
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {

    val context = LocalContext.current
    val mapView = remember {

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            setScrollableAreaLimitLatitude(
                MapView.getTileSystem().maxLatitude,
                MapView.getTileSystem().minLatitude,
                0
            )
            setScrollableAreaLimitLongitude(
                MapView.getTileSystem().minLongitude,
                MapView.getTileSystem().maxLongitude,
                0
            )

            isHorizontalMapRepetitionEnabled = true
            isVerticalMapRepetitionEnabled = true

            minZoomLevel = 4.5
            maxZoomLevel = 20.0
            controller.setZoom(minZoomLevel)
        }
    }

    val lifeCycleObserver = remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> {}
                else -> {}
            }
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, mapView) {
        lifecycle.addObserver(lifeCycleObserver)
        onDispose {
            lifecycle.removeObserver(lifeCycleObserver)
        }
    }

    return mapView
}

fun updateOrCreateUserMarker(
    mapView: MapView,
    geoPoint: GeoPoint,
    existingMarker: Marker?
): Marker {

    val finalMarker: Marker

    if (existingMarker == null) {
        finalMarker = Marker(mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Mi Ubicación"
        }
        mapView.overlays.add(finalMarker)
    } else {
        existingMarker.position = geoPoint
        finalMarker = existingMarker
    }

    mapView.invalidate()

    return finalMarker
}