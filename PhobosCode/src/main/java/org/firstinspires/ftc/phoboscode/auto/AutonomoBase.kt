package org.firstinspires.ftc.phoboscode.auto

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.roadrunner.geometry.Pose2d
import org.firstinspires.ftc.phoboscode.PhobosOpMode
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.phoboscode.vision.ConeSleevePipeline
import org.firstinspires.ftc.phoboscode.vision.SleevePattern
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvWebcam

abstract class AutonomoBase(val useVision: Boolean = true) : PhobosOpMode() {

    val drive get() = hardware.drive

    open val startPose = Pose2d()

    private var webcam: OpenCvWebcam? = null
    private val pipeline = ConeSleevePipeline()

    override fun setup() {
        if (useVision) {
            val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.packageName)
            webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName::class.java, "Webcam 1"), cameraMonitorViewId)

            // OR...  Do Not Activate the Camera Monitor View
            //webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"));

            webcam!!.setPipeline(pipeline)

            webcam!!.setMillisecondsPermissionTimeout(2500) // Timeout for obtaining permission is configurable. Set before opening.

            webcam!!.openCameraDeviceAsync(object : AsyncCameraOpenListener {
                override fun onOpened() {
                    webcam!!.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT)
                    FtcDashboard.getInstance().startCameraStream(webcam, 0.0)
                }

                override fun onError(errorCode: Int) { }
            })
        }
    }

    override fun begin() {
        webcam?.stopStreaming()

        drive.poseEstimate = startPose
        drive.followTrajectorySequenceAsync(sequence(pipeline.lastPattern))
    }

    abstract fun sequence(sleevePattern: SleevePattern): TrajectorySequence

}