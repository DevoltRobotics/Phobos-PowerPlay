package org.firstinspires.ftc.phoboscode.teleop

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.github.serivesmejia.deltacommander.command.DeltaInstantCmd
import com.github.serivesmejia.deltacommander.command.DeltaRunCmd
import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.github.serivesmejia.deltacommander.endRightAway
import com.github.serivesmejia.deltacommander.stopOn
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.phoboscode.PhobosOpMode
import org.firstinspires.ftc.phoboscode.command.intake.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToHighCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToLowCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToMidCmd
import org.firstinspires.ftc.phoboscode.command.mecanum.FieldCentricMecanumCmd
import org.firstinspires.ftc.phoboscode.command.mecanum.RobotCentricMecanumCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd
import org.firstinspires.ftc.phoboscode.rr.drive.StandardTrackingWheelLocalizer
import org.firstinspires.ftc.phoboscode.subsystem.IntakeArmSubsystem

@TeleOp(name = "TeleOp")
class PhobosTeleOp : PhobosOpMode() {

    override fun setup() {
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

        liftSubsystem.defaultCommand = LiftMoveCmd { (-gamepad2.left_stick_y).toDouble() * 0.7 }

        // lift positions
        superGamepad2.scheduleOnPress(Button.Y,
                LiftMoveToHighCmd().stopAfter(3.0)
        )

        superGamepad2.scheduleOnPress(Button.X,
                LiftMoveToMidCmd().stopAfter(3.0)
        )

        superGamepad2.scheduleOnPress(Button.A,
            deltaSequence {
               - LiftMoveToLowCmd().stopAfter(3.0).dontBlock()
               - TurretMoveToAngleCmd(0.0, endOnTargetReached = true).dontBlock()
               - IntakeZeroTiltCmd().endRightAway()
               - IntakeArmPositionMiddleCmd().endRightAway()
            }
        )

        // INTAKE

        intakeArmSubsystem.defaultCommand = IntakeArmPositionIncrementCmd { (-gamepad2.right_stick_y).toDouble() * 0.005 }

        superGamepad2.toggleScheduleOn(Button.B,
                IntakeTiltCmd(0.7).endRightAway(),
                IntakeZeroTiltCmd().endRightAway()
        )

        // TURRET

        turretSubsystem.defaultCommand = TurretMoveCmd { (gamepad2.left_trigger - gamepad2.right_trigger).toDouble() * 0.7 }

        // turret positions
        superGamepad2.scheduleOnPress(Button.DPAD_UP,
            TurretMoveToAngleCmd(0.0, endOnTargetReached = true)
        )

        superGamepad2.scheduleOnPress(Button.DPAD_LEFT,
            TurretMoveToAngleCmd(90.0, endOnTargetReached = true)
        )

        superGamepad2.scheduleOnPress(Button.DPAD_RIGHT,
            TurretMoveToAngleCmd(-90.0, endOnTargetReached = true)
        )

        // telemetry
        + DeltaRunCmd {
            telemetry.addData("turret pos", hardware.turretMotor.currentPosition)
            telemetry.addData("turret target", turretSubsystem.controller.targetPosition)

            telemetry.addData("lift pos", hardware.sliderLeftMotor.currentPosition)
            telemetry.addData("lift target", liftSubsystem.controller.targetPosition)

            telemetry.addData("fl", hardware.drive.leftFront.power)
            telemetry.addData("fl pos", hardware.drive.leftFront.currentPosition)
            telemetry.addData("fr", hardware.drive.rightFront.power)
            telemetry.addData("fr pos", hardware.drive.rightFront.currentPosition)
            telemetry.addData("bl", hardware.drive.leftRear.power)
            telemetry.addData("bl pos", hardware.drive.leftRear.currentPosition)
            telemetry.addData("br", hardware.drive.rightRear.power)
            telemetry.addData("br pos", hardware.drive.rightRear.currentPosition)


            val localizer = (hardware.drive.localizer as StandardTrackingWheelLocalizer)
            val odoPos = localizer.getWheelPositions()

            telemetry.addData("leftEncoder pos", localizer.leftEncoder.currentPosition)
            telemetry.addData("rightEncoder pos", localizer.rightEncoder.currentPosition)
            telemetry.addData("leftEncoder", odoPos[0])
            telemetry.addData("rightEncoder", odoPos[1])

            telemetry.addData("lift power", liftSubsystem.power)
            telemetry.addData("lift top pressed", hardware.sliderTopLimitSensor.isPressed)
            telemetry.addData("lift bottom pressed", hardware.sliderBottomLimitSensor.isPressed)

            telemetry.addData("arm", hardware.intakeArmServo.position)
            telemetry.addData("tilt", hardware.intakeTiltServo.position)

            telemetry.update()
        }
    }

}