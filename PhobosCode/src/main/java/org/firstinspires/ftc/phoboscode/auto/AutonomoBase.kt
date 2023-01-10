package org.firstinspires.ftc.phoboscode.auto

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.roadrunner.geometry.Pose2d
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.PhobosOpMode
import org.firstinspires.ftc.phoboscode.lastKnownAlliance
import org.firstinspires.ftc.phoboscode.lastKnownPose
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.phoboscode.vision.ConeSleevePipeline
import org.firstinspires.ftc.phoboscode.vision.SleevePattern
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvWebcam

abstract class AutonomoBase(val alliance: Alliance, val useVision: Boolean = true) : PhobosOpMode() {

    val drive get() = hardware.drive

    open val startPose = Pose2d()

    private var webcam: OpenCvWebcam? = null
    private val pipeline = ConeSleevePipeline()

    override fun setup() {
        // #freeodo
        hardware.odometryRetractServo.position = 0.8

        liftSubsystem.liftTurbo = 0.6

        if (useVision) {
            //val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.packageName)
            //webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName::class.java, "Webcam 1"), cameraMonitorViewId)

            // OR...  Do Not Activate the Camera Monitor View
            webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName::class.java,"Webcam 1"));

            webcam!!.setPipeline(pipeline)

            webcam!!.setMillisecondsPermissionTimeout(4000) // Timeout for obtaining permission is configurable. Set before opening.

            webcam!!.openCameraDeviceAsync(object : AsyncCameraOpenListener {
                override fun onOpened() {
                    webcam!!.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT)
                    FtcDashboard.getInstance().startCameraStream(webcam, 0.0)
                }

                override fun onError(errorCode: Int) { }
            })
        }
    }

    override fun initializeUpdate() {
        telemetry.addData("position", pipeline.lastPattern)
        telemetry.update()
    }

    override fun begin() {
        liftSubsystem.reset()
        turretSubsystem.reset()

        drive.poseEstimate = startPose
        drive.followTrajectorySequenceAsync(sequence(pipeline.lastPattern))
    }

    override fun runUpdate() {
        super.runUpdate()

        telemetry.addData("lift target", liftSubsystem.liftController.targetPosition)
        telemetry.addData("lift current", liftSubsystem.leftMotor.currentPosition)

        telemetry.addData("turret target", turretSubsystem.controller.targetPosition)
        telemetry.addData("turret current", turretSubsystem.motor.currentPosition)
        telemetry.update()

        if(!drive.isBusy) {
            requestOpModeStop()
        }

        lastKnownAlliance = alliance
        lastKnownPose = drive.poseEstimate
    }

    abstract fun sequence(sleevePattern: SleevePattern): TrajectorySequence

}