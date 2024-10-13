package com.indraazimi.mobpro2mhs.ui.screen

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2mhs.R
import com.indraazimi.mobpro2mhs.navigation.Screen
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ProfileScreen(user: MutableState<FirebaseUser?>, modifier: Modifier, navController: NavController) {
    val viewModel: DataViewModel = viewModel()

    val mahasiswa by viewModel.selectedMahasiswa.collectAsStateWithLifecycle()
    val kelas by viewModel.selectedKelas.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    var toggleClassDetailFragment by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = user.value?.uid) {
        user.value?.uid?.let { uid ->
            viewModel.getMahasiswaByID(uid)
        }
    }

    LaunchedEffect(key1 = mahasiswa) {
        mahasiswa?.id?.let { kelasId ->
            viewModel.getKelasByMahasiswaID(kelasId)
        }
    }

    if (loading && !isTablet) {
       Box(
           modifier = Modifier.fillMaxSize(),
           contentAlignment = Alignment.Center
       ) {
           CircularProgressIndicator()
       }
    } else {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(if (isTablet && toggleClassDetailFragment) 0.5f else 1f)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                ProfileCard(
                    photoUrl = mahasiswa?.fotoProfilUri ?: "",
                    name = mahasiswa?.nama ?: "",
                    nim = mahasiswa?.nim ?: "",
                    email = user.value?.email ?: "",
                    kelas = kelas?.nama ?: "",
                    address = mahasiswa?.address ?: ""
                )

                Button(onClick = {
                    if (!isTablet) {
                        navController.navigate(Screen.ClassDetail.withClassID(kelas?.id ?: ""))
                    } else {
                        toggleClassDetailFragment = !toggleClassDetailFragment
                    }
                }) {
                    var text by remember {
                        mutableStateOf(R.string.view_class_detail)
                    }

                    if (isTablet && toggleClassDetailFragment) {
                        text = R.string.hide_class_detail
                    } else {
                        text = R.string.view_class_detail
                    }

                    Text(text = stringResource(id = text))
                }

                mahasiswa?.let {
                    Osm(
                        modifier = modifier
                            .width(300.dp)
                            .height(300.dp),
                        pos = GeoPoint(it.latitude, it.longitude)
                    )
                }
            }

            if (isTablet && toggleClassDetailFragment) {
                ClassDetailFragment(
                    classId = kelas?.id ?: "",
                    navController = navController,
                    modifier = modifier
                        .fillMaxWidth(0.5f)
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    photoUrl: String?,
    name: String,
    nim: String,
    email: String,
    kelas: String,
    address: String,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(1.dp, Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray, CircleShape)
                ) {
                    Text(
                        text = nim,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$name ($nim)",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Text(
                text = kelas,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun Osm(modifier: Modifier = Modifier, pos: GeoPoint) {
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
            controller.setCenter(pos)
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
            }
            mapView.overlays.add(marker)
            currentMarker.value = marker
            mapView.invalidate()
        }
    }

    addOrMoveMarker(pos)

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