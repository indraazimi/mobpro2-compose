package com.indraazimi.mobpro2mhs.ui.screen

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.view.MotionEvent
import android.view.ViewConfiguration
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.indraazimi.mobpro2mhs.R
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier,
    lat: MutableState<Double?>,
    lon: MutableState<Double?>,
    address: MutableState<String>,
) {
    val coordinate = remember { mutableStateOf<GeoPoint?>(null) }

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

@SuppressLint("ClickableViewAccessibility")
@Composable
fun OsmMapView(modifier: Modifier, pos: MutableState<GeoPoint?>) {
    val context = LocalContext.current
    Configuration.getInstance().load(context, context.getSharedPreferences("osm", MODE_PRIVATE))

    val mapView = remember {
        MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setZoomInEnabled(true)
            zoomController.setZoomOutEnabled(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(-6.914744, 107.609810))
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