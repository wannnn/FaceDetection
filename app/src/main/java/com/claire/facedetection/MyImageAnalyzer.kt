package com.claire.facedetection

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

//class MyImageAnalyzer : ImageAnalysis.Analyzer {
//
//    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
//        0 -> FirebaseVisionImageMetadata.ROTATION_0
//        90 -> FirebaseVisionImageMetadata.ROTATION_90
//        180 -> FirebaseVisionImageMetadata.ROTATION_180
//        270 -> FirebaseVisionImageMetadata.ROTATION_270
//        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
//    }
//
//    override fun analyze(image: ImageProxy) {
//        val mediaImage = image.image
//        val imageRotation = degreesToFirebaseRotation(degrees)
//        if (mediaImage != null) {
//            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
//            // Pass image to an ML Kit Vision API
//            // ...
//        }
//    }
//}