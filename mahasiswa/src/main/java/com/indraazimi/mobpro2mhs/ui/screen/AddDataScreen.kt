package com.indraazimi.mobpro2mhs.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseUser
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.indraazimi.mobpro2mhs.R
import com.indraazimi.mobpro2mhs.navigation.Screen
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Mahasiswa
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val PROFILE_PHOTO_PATH = "mahasiswa/profile_photo"
const val FILE_DIR = "images"
const val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddDataScreen(
    navController: NavController,
    user: MutableState<FirebaseUser?>,
    modifier: Modifier = Modifier
) {
    val dataViewModel: DataViewModel = viewModel()

    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val classes by dataViewModel.allKelas.collectAsStateWithLifecycle()
    val selectedClass by dataViewModel.selectedKelas.collectAsStateWithLifecycle()
    val loading by dataViewModel.loading.collectAsStateWithLifecycle()

    val imageUri by dataViewModel.imageUri.collectAsStateWithLifecycle()

    var selectedClassId by remember { mutableStateOf("") }
    var nameData by remember { mutableStateOf("") }
    var nimData by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedClassName by remember { mutableStateOf("") }

    var isFaceDetected by remember { mutableStateOf(false) }
    var isCameraOpen by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val position = remember { mutableStateOf<GeoPoint?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraOpen = true
        } else {
            Toast.makeText(context, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = Unit) {
        dataViewModel.getAllKelas()
    }

    LaunchedEffect(key1 = selectedClassId.isNotEmpty()) {
        if (!loading) {
            dataViewModel.getKelasByID(selectedClass?.id ?: "")
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    LaunchedEffect(key1 = imageUri) {
        if (imageUri != null) {
            val newMahasiswa = Mahasiswa(
                id = user.value?.uid ?: "",
                nama = nameData,
                nim = nimData,
                fotoProfilUri = imageUri.toString(),
                latitude = position.value?.latitude ?: 0.0,
                longitude = position.value?.longitude ?: 0.0
            )

            dataViewModel.addMahasiswa(selectedClassId, newMahasiswa)

            navController.navigate(Screen.Profile.route)
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = stringResource(id = R.string.add_data))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameData,
            onValueChange = { nameData = it },
            label = { Text(stringResource(id = R.string.name)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nimData,
            onValueChange = { nimData = it },
            label = { Text(stringResource(id = R.string.student_id)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedClassName,
                onValueChange = {},
                label = { Text(stringResource(id = R.string.select_class)) },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                classes.forEach { kelas ->
                    DropdownMenuItem(
                        text = { Text(kelas.nama) },
                        onClick = {
                            selectedClassId = kelas.id
                            selectedClassName = kelas.nama
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isCameraOpen) {
            CameraPreviewView(
                onFaceDetected = { isFaceDetected = true },
                onFaceNotDetected = { isFaceDetected = false },
                onCloseCamera = { isCameraOpen = false },
                onImageCaptured = { uri ->
                    capturedImageUri = uri
                    isCameraOpen = false
                },
                isFaceDetected = isFaceDetected
            )
        } else {
            capturedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                ImagePreview(uri = uri)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!cameraPermissionState.hasPermission && cameraPermissionState.shouldShowRationale) {
                        cameraPermissionState.launchPermissionRequest()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    capturedImageUri = null
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (capturedImageUri == null) stringResource(id = R.string.open_camera) else stringResource(id = R.string.retake_photo))
            }
        }

        OsmMapView(modifier = modifier.width(
            if (isCameraOpen) 0.dp else 300.dp
        ).height(
            if (isCameraOpen) 0.dp else 300.dp
        ), pos = position)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                dataViewModel.uploadImage(capturedImageUri, context)
            },
            modifier = Modifier.align(Alignment.End),
            enabled = (nameData.isNotEmpty() && nimData.isNotEmpty() && selectedClassId.isNotEmpty() && capturedImageUri != null)
        ) {
            Text(stringResource(id = R.string.save))
        }
    }
}

@Composable
fun CameraPreviewView(
    onFaceDetected: () -> Unit,
    onFaceNotDetected: () -> Unit,
    onCloseCamera: () -> Unit,
    onImageCaptured: (Uri) -> Unit,
    isFaceDetected: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()

        val faceDetector = FaceDetection.getClient(faceDetectorOptions)

        val imageAnalysis = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(imageProxy, faceDetector, onFaceDetected, onFaceNotDetected)
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        if (isFaceDetected) {
            Button(
                onClick = {
                    imageCapture?.let {
                        captureImage(context, it) { uri ->
                            onImageCaptured(uri)
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Icon(Icons.Default.Face, contentDescription = stringResource(id = R.string.capture_photo))
            }
        }

        Button(
            onClick = {
                onCloseCamera()
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(id = R.string.close_camera))
        }
    }
}

fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit
) {
    val mediaDir = context.getExternalFilesDir(null)?.let {
        File(it, FILE_DIR).apply { mkdirs() }
    }
    val photoFile = File(mediaDir, TEMP_PHOTO_FILE_NAME)

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onImageCaptured(Uri.fromFile(photoFile))
            }
        }
    )
}

@SuppressLint("UnsafeOptInUsageError")
fun processImageProxy(
    imageProxy: ImageProxy,
    faceDetector: com.google.mlkit.vision.face.FaceDetector,
    onFaceDetected: () -> Unit,
    onFaceNotDetected: () -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    onFaceDetected()
                } else {
                    onFaceNotDetected()
                }
            }
            .addOnFailureListener {
                onFaceNotDetected()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ImagePreview(uri: Uri) {
    val context = LocalContext.current
    val bitmap = remember(uri) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.size(200.dp)
    )
}

@Composable
fun OsmMapView(modifier: Modifier = Modifier, pos: MutableState<GeoPoint?>) {
    val context = LocalContext.current

    Configuration.getInstance().load(context, context.getSharedPreferences("osm", MODE_PRIVATE))

    val mapView = remember {
        MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
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

    mapView.setOnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            val existingMarker = currentMarker.value

            if (existingMarker != null) {
                val existingGeoPoint = existingMarker.position
                val markerPositionInPixels = mapView.projection.toPixels(existingGeoPoint, null)
                val newGeoPoint = mapView.projection.fromPixels(motionEvent.x.toInt(), motionEvent.y.toInt())
                val newMarkerPositionInPixels = mapView.projection.toPixels(newGeoPoint, null)
                if (Math.abs(markerPositionInPixels.x - newMarkerPositionInPixels.x) < 10 && Math.abs(markerPositionInPixels.y - newMarkerPositionInPixels.y) < 10) {
                    removeMarker()
                } else {
                    addOrMoveMarker(newGeoPoint)
                }
            } else {
                addOrMoveMarker(mapView.projection.fromPixels(motionEvent.x.toInt(), motionEvent.y.toInt()))
            }
        }
        mapView.performClick()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newCenter = mapView.projection.fromPixels(
                        mapView.width / 2 - dragAmount.x.toInt(),
                        mapView.height / 2 - dragAmount.y.toInt()
                    )

                    mapView.controller.setCenter(newCenter)
                    mapView.zoomController.setZoomInEnabled(true)
                    mapView.zoomController.setZoomOutEnabled(true)
                    mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                }
            }
    )

    DisposableEffect(Unit) {
        onDispose {
            Configuration.getInstance().save(context, context.getSharedPreferences("osm", MODE_PRIVATE))
            mapView.onDetach()
        }
    }
}