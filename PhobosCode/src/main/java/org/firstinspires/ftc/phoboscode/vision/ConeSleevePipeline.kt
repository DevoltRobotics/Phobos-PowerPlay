package org.firstinspires.ftc.phoboscode.vision

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.openftc.apriltag.AprilTagDetection
import kotlin.math.round

enum class SleevePattern { A, B, C }

class ConeSleevePipeline : AprilTagDetectionPipeline() {

    var lastPattern = SleevePattern.A
        private set

    override fun processFrame(input: Mat?): Mat {
        val output = super.processFrame(input)

        var biggestDetection: AprilTagDetection? = null
        var biggestDist = 0.0

        for(detection in latestDetections) {
            val cornerA = detection.corners[0].x
            val cornerB = detection.corners[1].x
            val dist = cornerB - cornerA

            if(biggestDetection == null) {
                biggestDetection = detection
            } else if(dist > biggestDist) {
                biggestDetection = detection
                biggestDist = dist
            }
        }

        biggestDetection?.let {
            drawTextOutline(output, "Sleeve", it.corners[0], 12.0, 1.0)
        }

        return output
    }

    private fun drawTextOutline(input: Mat, text: String, position: Point, textSize: Double, thickness: Double) {

        // Outline
        Imgproc.putText(
                input,
                text,
                position,
                Imgproc.FONT_HERSHEY_PLAIN,
                textSize,
                Scalar(255.0, 255.0, 255.0),
                round(thickness).toInt()
        )

        //Text
        Imgproc.putText(
                input,
                text,
                position,
                Imgproc.FONT_HERSHEY_PLAIN,
                textSize,
                Scalar(0.0, 0.0, 0.0),
                round(thickness * (0.2)).toInt()
        )

    }

}