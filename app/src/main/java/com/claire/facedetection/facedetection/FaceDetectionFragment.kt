package com.claire.facedetection.facedetection

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.claire.facedetection.MainActivity.Companion.getOutputDirectory
import com.claire.facedetection.base.BaseFragment
import com.claire.facedetection.databinding.FragFaceDetectionBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceDetectionFragment : BaseFragment<FragFaceDetectionBinding>() {

    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> by lazy {
        ProcessCameraProvider.getInstance(requireContext())
    }
    private var imageCapture: ImageCapture? = null

    private val outputDirectory: File by lazy {
        getOutputDirectory(requireContext())
    }

    private val executor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }


    override fun bindView(inflater: LayoutInflater, container: ViewGroup?
    ): FragFaceDetectionBinding =
        FragFaceDetectionBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()
        binding.btnTakePhoto.setOnClickListener {
            takePhoto()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        executor.shutdown()
    }

    private fun startCamera() {

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))

    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(binding.cameraView.createSurfaceProvider())

        imageCapture = ImageCapture.Builder()
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(executor, MyImageAnalyzer())
            }

        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer)

    }

    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.TAIWAN
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}