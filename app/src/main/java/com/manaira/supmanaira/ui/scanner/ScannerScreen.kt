package com.manaira.supmanaira.ui.scanner

import android.util.Log
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ScannerScreen(
    navController: NavHostController,
    registroId: Int
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember { mutableStateOf(false) }
    var isCameraReady by remember { mutableStateOf(false) }

    // Controle de leitura
    var scanLocked by remember { mutableStateOf(false) }
    var lastCode by remember { mutableStateOf<String?>(null) }
    var stableCount by remember { mutableStateOf(0) }
    var lastAnalysisTime by remember { mutableStateOf(0L) }

    // ---------------------------
    // PERMISSÃO
    // ---------------------------
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) hasCameraPermission = true
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        if (!hasCameraPermission) {
            Text("Permissão da câmera é necessária para ler códigos de barras.")
            return@Box
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->

                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({

                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .setTargetResolution(Size(1080, 1920))
                        .build()
                        .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                    val selector = CameraSelector.DEFAULT_BACK_CAMERA

                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetResolution(Size(1280, 720))
                        .build()

                    val scanner = BarcodeScanning.getClient()

                    analysis.setAnalyzer(
                        ContextCompat.getMainExecutor(ctx)
                    ) { imageProxy ->

                        if (scanLocked) {
                            imageProxy.close(); return@setAnalyzer
                        }

                        // --------------------------
                        // LIMITA A FREQUÊNCIA (80 ms)
                        // --------------------------
                        val now = System.currentTimeMillis()
                        if (now - lastAnalysisTime < 80) {
                            imageProxy.close()
                            return@setAnalyzer
                        }
                        lastAnalysisTime = now

                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {

                            val image = InputImage.fromMediaImage(
                                mediaImage, imageProxy.imageInfo.rotationDegrees
                            )

                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->

                                    val detected = barcodes.firstOrNull()?.displayValue

                                    if (!detected.isNullOrBlank()) {

                                        // --------------------------
                                        // ESTABILIZAÇÃO REAL
                                        // --------------------------
                                        if (detected == lastCode) {
                                            stableCount++
                                        } else {
                                            lastCode = detected
                                            stableCount = 1
                                        }

                                        if (stableCount >= 5) { // precisa repetir 5 frames
                                            scanLocked = true

                                            scope.launch {
                                                delay(300)

                                                Log.d("DEBUG_SCANNER", "Código estabilizado e enviado: $detected")

                                                navController.previousBackStackEntry
                                                    ?.savedStateHandle
                                                    ?.set("scanned_code", detected)

                                                Log.d("DEBUG_SCANNER", "Valor gravado no savedStateHandle")

                                                navController.popBackStack()
                                            }
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            analysis
                        )
                        isCameraReady = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isCameraReady = false
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        if (!isCameraReady) {
            CircularProgressIndicator()
        }
    }
}
