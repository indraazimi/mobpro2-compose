package com.indraazimi.mobpro2mhs.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.location.Location
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.indraazimi.mobpro2mhs.R
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier,
    lat: MutableState<Double?>,
    lon: MutableState<Double?>,
    address: MutableState<String>,
) {
    val coordinate = remember { mutableStateOf<GeoPoint?>(null) }
    val myLocation = remember { mutableStateOf<GeoPoint?>(null) }

    val context = LocalContext.current

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(key1 = locationPermissionState) {
        if (locationPermissionState.hasPermission) {
            getCurrentLocation(context) { location ->
                myLocation.value = location?.let { GeoPoint(it.latitude, it.longitude) }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(context) { location ->
                myLocation.value = location?.let { GeoPoint(it.latitude, it.longitude) }
            }
        } else {
            Toast.makeText(context, R.string.location_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionState.hasPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = address.value,
            onValueChange = {
                address.value = it
            },
            label = { Text(stringResource(id = R.string.address)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .heightIn(min = 56.dp)
        )

        OsmMapView(
            modifier = modifier
                .fillMaxSize()
                .weight(1f),
            pos = coordinate,
            myLocation = myLocation
        )

        Button(
            onClick = {
                lat.value = coordinate.value?.latitude
                lon.value = coordinate.value?.longitude
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Save")
        }
    }
}

fun getCurrentLocation(context: Context, onLocationReceived: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        onLocationReceived(null)
        return
    }

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000
    ).setMinUpdateIntervalMillis(5000).build()

    fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            onLocationReceived(location)
            fusedLocationClient.removeLocationUpdates(this)
        }
    }, null)
}

@SuppressLint("ClickableViewAccessibility")
@Composable
fun OsmMapView(modifier: Modifier, pos: MutableState<GeoPoint?>, myLocation: MutableState<GeoPoint?>) {
    val context = LocalContext.current
    Configuration.getInstance().load(context, context.getSharedPreferences("osm", MODE_PRIVATE))

    var myLocationMarker: Marker? by remember { mutableStateOf(null) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setZoomInEnabled(true)
            zoomController.setZoomOutEnabled(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
            controller.setZoom(15.0)
        }
    }

    val currentMarker = remember { mutableStateOf<Marker?>(null) }

    fun addOrMoveMarker(geoPoint: IGeoPoint) {
        currentMarker.value?.let { existingMarker ->
            existingMarker.position = geoPoint as GeoPoint
            mapView.invalidate()
        } ?: run {
            val marker = Marker(mapView).apply {
                position = geoPoint as GeoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                pos.value = geoPoint
            }
            mapView.overlays.add(marker)
            currentMarker.value = marker
            mapView.invalidate()
        }
    }

    if (myLocation.value != null) {
        if (myLocationMarker == null) {
            myLocationMarker = Marker(mapView).apply {
                position = myLocation.value
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = context.getDrawable(
                    org.osmdroid.library.R.drawable.person
                )
            }
            mapView.overlays.add(myLocationMarker)
        } else {
            myLocationMarker?.position = myLocation.value
        }
        mapView.controller.setCenter(myLocation.value)
    } else {
        mapView.controller.setCenter(GeoPoint(-6.914744, 107.609810))
    }

    fun removeMarker() {
        currentMarker.value?.let { marker ->
            mapView.overlays.remove(marker)
            currentMarker.value = null
            mapView.invalidate()
        }
    }

    var lastTouchDown = remember { 0L }

    mapView.setOnTouchListener { _, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchDown = System.currentTimeMillis()
            }
            MotionEvent.ACTION_UP -> {
                val touchDuration = System.currentTimeMillis() - lastTouchDown
                if (touchDuration < ViewConfiguration.getTapTimeout()) {
                    val tappedPoint = mapView.projection.fromPixels(motionEvent.x.toInt(), motionEvent.y.toInt())
                    val existingMarker = currentMarker.value
                    if (existingMarker != null) {
                        val existingGeoPoint = existingMarker.position
                        val markerPositionInPixels = mapView.projection.toPixels(existingGeoPoint, null)
                        val tappedPointInPixels = mapView.projection.toPixels(tappedPoint, null)
                        val distance = sqrt(
                            (markerPositionInPixels.x - tappedPointInPixels.x).toDouble().pow(2.0) +
                                    (markerPositionInPixels.y - tappedPointInPixels.y).toDouble()
                                        .pow(2.0)
                        )
                        if (distance < 50) {
                            removeMarker()
                        } else {
                            addOrMoveMarker(tappedPoint)
                        }
                    } else {
                        addOrMoveMarker(tappedPoint)
                    }
                }
            }
        }
        mapView.performClick()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            Configuration.getInstance().save(context, context.getSharedPreferences("osm", MODE_PRIVATE))
            mapView.onDetach()
        }
    }
}