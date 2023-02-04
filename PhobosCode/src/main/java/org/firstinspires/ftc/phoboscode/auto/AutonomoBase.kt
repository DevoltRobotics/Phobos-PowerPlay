package org.firstinspires.ftc.phoboscode.auto

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.roadrunner.geometry.Pose2d
import org.firstinspires.ftc.phoboscode.*
import org.firstinspires.ftc.phoboscode.hardware.UltraSonicRelocalizer
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.phoboscode.vision.ConeSleevePipeline
import org.firstinspires.ftc.phoboscode.vision.SleevePattern
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvWebcam

abstract class AutonomoBase(val alliance: Alliance, val side: Side, val useVision: Boolean = true) : PhobosOpMode() {

    val drive get() = hardware.drive

    val ultraSonicRelocalizer by lazy { UltraSonicRelocalizer(hardware.leftMBUltraSonic, hardware.rightMBUltraSonic, side) }

    open val startPose = Pose2d()

    private var lastRelocalizeX = 0.0

    private var webcam: OpenCvWebcam? = null
    private val pipeline = ConeSleevePipeline()

    override fun setup() {
        // #freeodo
        hardware.odometryRetractServo.position = 0.8

        liftSubsystem.liftTurbo = 0.6
        intakeArmSubsystem.tiltSaveThreshold = 0.75

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
        telemetry.addData("x relocalization", ultraSonicRelocalizer.xEstimate)
        telemetry.update()
    }

    override fun begin() {
        liftSubsystem.reset()
        turretSubsystem.reset()

        drive.poseEstimate = startPose
        drive.followTrajectorySequenceAsync(sequence(pipeline.lastPattern))

        relocalizeXEstimate()
    }

    override fun runUpdate() {
        super.runUpdate()

        telemetry.addData("lift target", liftSubsystem.liftController.targetPosition)
        telemetry.addData("lift current", liftSubsystem.leftMotor.currentPosition)

        telemetry.addData("turret target", turretSubsystem.controller.targetPosition)
        telemetry.addData("turret current", turretSubsystem.motor.currentPosition)
        telemetry.addData("last x relocalization", lastRelocalizeX)
        telemetry.update()

        if(!drive.isBusy) {
            requestOpModeStop()
        }

        lastKnownAlliance = alliance
    }

    fun relocalizeXEstimate(offset: Double = 0.0) {
        ultraSonicRelocalizer.relocalize(drive.localizer, offset)
        lastRelocalizeX = drive.localizer.poseEstimate.x
    }

    abstract fun sequence(sleevePattern: SleevePattern): TrajectorySequence

}