package com.indraazimi.mobpro2mhs.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
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
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val PROFILE_PHOTO_PATH = "mahasiswa/profile_photo"
const val FILE_DIR = "images"
const val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"
const val CROPPED_PHOTO_FILE_NAME = "cropped_photo.jpg"

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddDataScreen(
    navController: NavController,
    user: MutableState<FirebaseUser?>,
    modifier: Modifier = Modifier,
    lat: MutableState<Double?>,
    lon: MutableState<Double?>,
    address: MutableState<String>
) {
    val dataViewModel: DataViewModel = viewModel()

    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val classes by dataViewModel.allKelas.collectAsStateWithLifecycle()
    val selectedClass by dataViewModel.selectedKelas.collectAsStateWithLifecycle()
    val loading by dataViewModel.loading.collectAsStateWithLifecycle()

    val imageUri by dataViewModel.imageUri.collectAsStateWithLifecycle()

    var selectedClassId by rememberSaveable { mutableStateOf("") }
    var nameData by rememberSaveable { mutableStateOf("") }
    var nimData by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedClassName by rememberSaveable { mutableStateOf("") }

    var isFaceDetected by remember { mutableStateOf(false) }
    var isCameraOpen by remember { mutableStateOf(false) }
    var capturedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

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
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
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
                latitude = lat.value ?: 0.0,
                longitude = lon.value ?: 0.0,
                address = address.value
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Map.route)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(id = R.string.select_location))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (lat.value != null && lon.value != null && address.value.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "${stringResource(id = R.string.address)} : ${address.value}")
                Text(text = "${stringResource(id = R.string.latitude)} : ${lat.value}")
                Text(text = "${stringResource(id = R.string.longitude)} : ${lon.value}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                dataViewModel.uploadImage(capturedImageUri, context)
            },
            modifier = Modifier.align(Alignment.End),
            enabled = (
                nameData.isNotEmpty() &&
                nimData.isNotEmpty() &&
                selectedClassId.isNotEmpty() &&
                capturedImageUri != null &&
                lat.value != null &&
                lon.value != null &&
                address.value.isNotEmpty()
            )
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
    var isLoading by remember { mutableStateOf(false) }

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
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

            if (isFaceDetected) {
                Button(
                    onClick = {
                        imageCapture?.let {
                            isLoading = true
                            captureImage(context, it) { uri ->
                                onImageCaptured(uri)
                                onCloseCamera()
                                isLoading = false
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
                val imageUri = Uri.fromFile(photoFile)

                detectAndCropFace(context, imageUri) { croppedUri ->
                    onImageCaptured(croppedUri)
                }
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
fun detectAndCropFace(context: Context, uri: Uri, onCropped: (Uri) -> Unit) {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source)

    val image = InputImage.fromBitmap(bitmap, 0)

    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()

    val detector = FaceDetection.getClient(options)

    detector.process(image)
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                val face = faces[0]
                val bounds = face.boundingBox

                val paddingFactor = 0.4f

                val faceWidth = bounds.width()
                val faceHeight = bounds.height()

                val size = (maxOf(faceWidth, faceHeight) * (1 + paddingFactor)).toInt()

                val centerX = bounds.centerX()
                val centerY = bounds.centerY()

                val left = (centerX - size / 2).coerceAtLeast(0)
                val top = (centerY - size / 2).coerceAtLeast(0)
                val right = (centerX + size / 2).coerceAtMost(bitmap.width)
                val bottom = (centerY + size / 2).coerceAtMost(bitmap.height)

                val cropWidth = right - left
                val cropHeight = bottom - top

                val croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    left,
                    top,
                    cropWidth,
                    cropHeight
                )

                val croppedFile = File(context.getExternalFilesDir(null), CROPPED_PHOTO_FILE_NAME)
                FileOutputStream(croppedFile).use { out ->
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }

                onCropped(Uri.fromFile(croppedFile))
            } else {
                onCropped(uri)
            }
        }
        .addOnFailureListener {
            it.printStackTrace()
            onCropped(uri)
        }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ImagePreview(uri: Uri) {
    val context = LocalContext.current
    val bitmap = remember(uri) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .aspectRatio(1f)

    )
}