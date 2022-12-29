package org.firstinspires.ftc.phoboscode.teleop

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.github.serivesmejia.deltacommander.command.DeltaInstantCmd
import com.github.serivesmejia.deltacommander.command.DeltaRunCmd
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.github.serivesmejia.deltacommander.dsl.deltaSequenceInstant
import com.github.serivesmejia.deltacommander.endRightAway
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.phoboscode.PhobosOpMode
import org.firstinspires.ftc.phoboscode.command.intake.*
import org.firstinspires.ftc.phoboscode.command.lift.*
import org.firstinspires.ftc.phoboscode.command.mecanum.FieldCentricMecanumCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretConeTrackingCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd
import org.firstinspires.ftc.phoboscode.lastKnownAlliance
import org.firstinspires.ftc.phoboscode.lastKnownPose
import org.firstinspires.ftc.phoboscode.rr.drive.StandardTrackingWheelLocalizer
import org.firstinspires.ftc.phoboscode.vision.ConeTrackingPipeline
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import kotlin.math.abs

@TeleOp(name = "Nacho Libre")
class PhobosTeleOp : PhobosOpMode() {

    val coneTrackingPipeline = ConeTrackingPipeline()

    override fun setup() {
        // retract odo
        hardware.odometryRetractServo.position = 0.0

        // OR...  Do Not Activate the Camera Monitor View
        val webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName::class.java,"Webcam 1"));

        webcam.setPipeline(coneTrackingPipeline)

        webcam.setMillisecondsPermissionTimeout(4000) // Timeout for obtaining permission is configurable. Set before opening.

        webcam.openCameraDeviceAsync(object : OpenCvCamera.AsyncCameraOpenListener {
            override fun onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)
                FtcDashboard.getInstance().startCameraStream(webcam, 0.0)

                webcam.focusControl.mode = FocusControl.Mode.Fixed;

                webcam.focusControl.focusLength = 1.0;
            }

            override fun onError(errorCode: Int) { }
        })

        hardware.drive.poseEstimate = lastKnownPose.plus(Pose2d(0.0, 0.0, lastKnownAlliance.angleOffset))

        intakeArmSubsystem.reset()
        liftSubsystem.reset()

        /* START A */

        // MECANUM
        superGamepad1.scheduleOnPress(Button.DPAD_UP, DeltaInstantCmd {
            hardware.drive.poseEstimate = Pose2d()
        })

        + FieldCentricMecanumCmd(gamepad1)

        // INTAKE

        superGamepad1.scheduleOn(Button.A,
                IntakeWheelsAbsorbCmd(),
                IntakeWheelsHoldCmd()
        )

        superGamepad1.scheduleOn(Button.B,
                IntakeWheelsReleaseCmd(),
                IntakeWheelsHoldCmd()
        )

        superGamepad1.scheduleOn(Button.RIGHT_BUMPER,
            IntakeWheelsAbsorbCmd(),
            IntakeWheelsHoldCmd()
        )

        superGamepad1.scheduleOn(Button.LEFT_BUMPER,
            IntakeWheelsReleaseCmd(),
            IntakeWheelsHoldCmd()
        )

        /* START B */

        // LIFT

        liftSubsystem.defaultCommand = LiftMoveCmd { (-gamepad2.left_stick_y).toDouble()  }

        + DeltaRunCmd {
            if(abs(gamepad2.left_stick_y) > 0.5) {
                liftSubsystem.free()
            }
        }

        // lift positions
        superGamepad2.scheduleOnPress(Button.Y,
            deltaSequenceInstant {
                - IntakeArmPositionSaveCmd().endRightAway().dontBlock()
                - waitForSeconds(0.2)
                - LiftMoveToHighCmd().dontBlock()
            }
        )

        superGamepad2.scheduleOnPress(Button.X,
            deltaSequenceInstant {
                - IntakeArmPositionSaveCmd().endRightAway().dontBlock()
                - waitForSeconds(0.2)
                - LiftMoveToMidCmd().dontBlock()
            }
        )

        superGamepad2.scheduleOnPress(Button.A,
            deltaSequenceInstant {
                - IntakeArmPositionSaveCmd().endRightAway().dontBlock()
                - waitForSeconds(0.2)
                - LiftMoveDownCmd().dontBlock()
            }
        )

        superGamepad2.scheduleOnPress(Button.DPAD_DOWN,
            deltaSequenceInstant {
                - LiftMoveDownCmd().dontBlock()
                - TurretMoveToAngleCmd(0.0).dontBlock()
                - IntakeZeroTiltCmd().endRightAway()
                - IntakeArmPositionMiddleCmd().endRightAway()
            }
        )

        // INTAKE

        intakeArmSubsystem.defaultCommand = IntakeArmPositionIncrementCmd { (-gamepad2.right_stick_y).toDouble() * 0.015 }

        //superGamepad2.toggleScheduleOn(Button.B,
        //        IntakeTiltCmd(0.7).endRightAway(),
        //        IntakeZeroTiltCmd().endRightAway()
        //)

        // TURRET

        turretSubsystem.defaultCommand = TurretMoveCmd { (gamepad2.left_trigger - gamepad2.right_trigger).toDouble() * 0.65 }

        + DeltaRunCmd {
            if(abs(gamepad2.left_trigger - gamepad2.right_trigger) >= 0.5) {
                turretSubsystem.free()
            }
        }

        superGamepad2.toggleScheduleOn(Button.B,
            TurretMoveCmd(0.0),
            TurretConeTrackingCmd(coneTrackingPipeline)
        )

        // turret positions
        superGamepad2.scheduleOnPress(Button.DPAD_UP,
            deltaSequenceInstant {
                - IntakeArmPositionSaveCmd().endRightAway().dontBlock()
                - waitForSeconds(0.2)
                - TurretMoveToAngleCmd(0.0).dontBlock()
            }
        )

        superGamepad2.scheduleOnPress(Button.DPAD_LEFT,
            deltaSequenceInstant {
                - IntakeArmPositionSaveCmd().endRightAway().dontBlock()
                - waitForSeconds(0.2)
                - TurretMoveToAngleCmd(90.0).dontBlock()
            }
        )

        superGamepad2.scheduleOnPress(Button.DPAD_RIGHT,
            deltaSequenceInstant {
                - IntakeArmPositionSaveCmd().endRightAway().dontBlock()
                - waitForSeconds(0.2)
                - TurretMoveToAngleCmd(-90.0).dontBlock()
            }
        )

        // telemetry
        + DeltaRunCmd {
            telemetry.addData("turret angle", turretSubsystem.angle)
            telemetry.addData("turret pos", hardware.turretMotor.currentPosition)
            telemetry.addData("turret target", turretSubsystem.controller.targetPosition)

            telemetry.addData("lift pos", hardware.sliderLeftMotor.currentPosition)
            telemetry.addData("lift target", liftSubsystem.liftController.targetPosition)

            telemetry.addData("fl", hardware.drive.leftFront.power)
            telemetry.addData("fl pos", hardware.drive.leftFront.currentPosition)
            telemetry.addData("fr", hardware.drive.rightFront.power)
            telemetry.addData("fr pos", hardware.drive.rightFront.currentPosition)
            telemetry.addData("bl", hardware.drive.leftRear.power)
            telemetry.addData("bl pos", hardware.drive.leftRear.currentPosition)
            telemetry.addData("br", hardware.drive.rightRear.power)
            telemetry.addData("br pos", hardware.drive.rightRear.currentPosition)

            telemetry.addData("lift power", liftSubsystem.power)
            telemetry.addData("lift top red", liftSubsystem.lastTopRed)
            telemetry.addData("lift bottom pressed", hardware.sliderBottomLimitSensor.isPressed)

            telemetry.addData("arm", hardware.intakeArmServo.position)
            telemetry.addData("tilt", hardware.intakeTiltServo.position)

            telemetry.update()
        }
    }

}