package com.claire.facedetection.facedetection

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

class MyImageAnalyzer : ImageAnalysis.Analyzer {

    private val faceDetector: FirebaseVisionFaceDetector by lazy {
        // Real-time contour detection
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()
        FirebaseVision.getInstance().getVisionFaceDetector(options)
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {

        image.image?.let { img ->

            // Extract the mediaImage from the ImageProxy
            val visionImage = FirebaseVisionImage.fromMediaImage(
                img,
                degreesToFirebaseRotation(image.imageInfo.rotationDegrees)
            )

            // Pass image to an ML Kit Vision API
            faceDetector.detectInImage(visionImage)
                .addOnSuccessListener { faces ->
                    // Returns an array containing all the detected faces
                    faces.forEach { face ->
                        // Loop through the array and detect features
                        println("Smiling probability ${face.smilingProbability}")
                        println("Left eye open probability ${face.leftEyeOpenProbability}")
                        println("Right eye open probability ${face.rightEyeOpenProbability}")
                        println("Tracking id ${face.trackingId}")
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }
}