package pl.gmat.cameratask

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val CAMERA_PERMISSION_REQUEST_CODE = 100

private const val HORIZONTAL_MARGIN = 100
private const val VERTICAL_MARGIN = 300

class MainActivity : AppCompatActivity() {

    private lateinit var imageCaptureUseCase: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (hasCameraPermission()) {
            initCamera()
        } else {
            requestCameraPermission()
        }
        findViewById<Button>(R.id.takePhotoButton).setOnClickListener { onTakePhotoClicked() }
    }

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_DENIED

    private fun requestCameraPermission() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )

    private fun initCamera() =
        ProcessCameraProvider.getInstance(this).run {
            addListener(
                Runnable { bindCameraPreview(get()) },
                ContextCompat.getMainExecutor(this@MainActivity)
            )
        }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        val previewView = findViewById<PreviewView>(R.id.previewView)
        val previewUseCase = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        imageCaptureUseCase = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
        val camera = cameraProvider.bindToLifecycle(
            this,
            cameraSelector,
            previewUseCase,
            imageCaptureUseCase
        )
        previewUseCase.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
    }

    private fun onTakePhotoClicked() = imageCaptureUseCase.takePicture(
        ContextCompat.getMainExecutor(this),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                ImageCache.bitmap = image.toBitmap().process(HORIZONTAL_MARGIN, VERTICAL_MARGIN)
                super.onCaptureSuccess(image)
                startActivity(Intent(this@MainActivity, ResultActivity::class.java))
            }
        }
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initCamera()
        }
    }
}
