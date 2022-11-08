package org.firstinspires.ftc.phoboscode.vision

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.openftc.apriltag.AprilTagDetection
import kotlin.math.round

enum class SleevePattern { A, B, C }

class ConeSleevePipeline : AprilTagDetectionPipeline() {

    companion object {

        val DECIMATION_HIGH = 3f
        val DECIMATION_LOW = 2f
        val THRESHOLD_HIGH_DECIMATION_RANGE_METERS = 1.0f
        val THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION = 4

    }

    private var numFramesWithoutDetection = 0

    var lastPattern = SleevePattern.B
        private set

    override fun processFrame(input: Mat?): Mat {
        val output = super.processFrame(input)

        val detections = detectionsUpdate

        // If there's been a new frame...
        if(detections != null)
        {
            // If we don't see any tags
            if(detections.size == 0)
            {
                numFramesWithoutDetection++;

                // If we haven't seen a tag for a few frames, lower the decimation
                // so we can hopefully pick one up if we're e.g. far back
                if(numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION)
                {
                    setDecimation(DECIMATION_LOW);
                }
            }
            // We do see tags!
            else
            {
                numFramesWithoutDetection = 0;

                // If the target is within 1 meter, turn on high decimation to
                // increase the frame rate
                if(detections[0].pose.z < THRESHOLD_HIGH_DECIMATION_RANGE_METERS)
                {
                    setDecimation(DECIMATION_HIGH);
                }
            }
        }

        var biggestDetection: AprilTagDetection? = null
        var biggestDist = 0.0

        for(detection in detections) {
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

            lastPattern = when(it.id) {
                1 -> SleevePattern.A
                3 -> SleevePattern.C
                else -> SleevePattern.B
            }
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