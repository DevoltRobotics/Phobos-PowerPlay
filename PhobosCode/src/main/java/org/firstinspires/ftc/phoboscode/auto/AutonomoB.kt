package org.firstinspires.ftc.phoboscode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.github.serivesmejia.deltacommander.endRightAway
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.command.intake.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveDownCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToHighCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToPosCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd
import org.firstinspires.ftc.phoboscode.rr.drive.DriveConstants
import org.firstinspires.ftc.phoboscode.rr.drive.SampleMecanumDrive
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequenceBuilder
import org.firstinspires.ftc.phoboscode.subsystem.Lift
import org.firstinspires.ftc.phoboscode.vision.SleevePattern
import org.firstinspires.ftc.phoboscode.vision.SleevePattern.*

abstract class AutonomoB(
    alliance: Alliance,
    val cycles: Int = 4
) : AutonomoBase(alliance) {

    override val startPose = Pose2d(35.0, -58.0, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
        }

        // prepare for putting preload cone
        UNSTABLE_addTemporalMarkerOffset(0.8) { + prepareForPuttingCone(90.0) }
        lineToConstantHeading(Vector2d(35.5, 4.2)) // TODO: Preload cone score position

        UNSTABLE_addTemporalMarkerOffset(0.0) { + IntakeArmPositionCmd(0.56) }
        UNSTABLE_addTemporalMarkerOffset(0.2) { + IntakeWheelsReleaseCmd() }
        waitSeconds(0.9)

        var liftHeight = 590.0

        UNSTABLE_addTemporalMarkerOffset(0.2) {
            + IntakeArmPositionSaveCmd()
            + IntakeWheelsStopCmd()

            + LiftMoveToPosCmd(liftHeight)
            + TurretMoveToAngleCmd(-90.0)
        }

        waitSeconds(0.5)

        // just park here when we won`t be doing any cycles
        if(cycles == 0) {
            // park
            when(sleevePattern) {
                A -> {
                    lineToSplineHeading(Pose2d(35.0, -33.0, Math.toRadians(0.0)))

                    setReversed(true)
                    splineToConstantHeading(Vector2d(58.0, -35.0), Math.toRadians(180.0))
                    setReversed(false)
                }
                B -> {
                    lineToLinearHeading(Pose2d(35.0, -35.0, Math.toRadians(0.0)))
                }
                C -> {
                    lineToSplineHeading(Pose2d(35.0, -33.0, Math.toRadians(0.0)))
                    lineToConstantHeading(Vector2d(11.5, -35.0))

                    turn(Math.toRadians(-90.0))
                }
            }

            return@apply
        }

        val grabX = 57.2 // TODO: Grab coordinates
        var grabY = -5.9

        setReversed(true)
        splineToConstantHeading(Vector2d(45.0, grabY), Math.toRadians(0.0))

        UNSTABLE_addTemporalMarkerOffset(0.2) {
            + deltaSequence {
                - IntakeArmPositionCmd(0.55).dontBlock()
                - waitForSeconds(0.3)
                - IntakeTiltCmd(0.58).dontBlock()
            }
        }

        UNSTABLE_addTemporalMarkerOffset(0.3) {
            + IntakeWheelsAbsorbCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.4) {
            + IntakeArmPositionCmd(0.44)
        }

        lineToConstantHeading(Vector2d(grabX, grabY))
        setReversed(false)

        waitSeconds(0.7)

        repeat(cycles - 1) {
            liftHeight -= 50

            putOnHigh(-90.0, liftHeight)

            UNSTABLE_addTemporalMarkerOffset(0.8) {
                + IntakeWheelsAbsorbCmd()
            }

            UNSTABLE_addTemporalMarkerOffset(1.0) {
                + IntakeArmPositionCmd(0.46)
            }

            UNSTABLE_addTemporalMarkerOffset(1.3) {
                + IntakeArmPositionCmd(0.44)
            }

            lineToSplineHeading(Pose2d(grabX, grabY, Math.toRadians(90.0)))

            waitSeconds(0.4)

            grabY -= 0.2
        }

        putOnHigh(endingLiftPos = 0.0, endingTurretAngle = 0.0)

        when(sleevePattern) {
            A -> {
                lineToLinearHeading(Pose2d(12.0, -7.3, Math.toRadians(90.0)))
            }
            B -> {
                lineToLinearHeading(Pose2d(35.0, -7.3, Math.toRadians(90.0)))
            }
            C -> {
                lineToLinearHeading(Pose2d(56.0, -7.3, Math.toRadians(90.0)))
            }
        }

        waitSeconds(5.0)
    }.build()

    fun prepareForPuttingCone(turretAngle: Double, liftPos: Int = Lift.highPos) = deltaSequence {
        - TurretMoveToAngleCmd(turretAngle).dontBlock()

        - waitForSeconds(0.1)

        - LiftMoveToPosCmd(liftPos.toDouble()).dontBlock()
    }

    fun TrajectorySequenceBuilder.putOnHigh(endingTurretAngle: Double, endingLiftPos: Double? = null) {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
            + IntakeWheelsHoldCmd()
        }
        UNSTABLE_addTemporalMarkerOffset(0.2) { // TODO: tiempo para que se mueva la torreta
            + prepareForPuttingCone(40.0, Lift.highPos + 40)
        }

        UNSTABLE_addTemporalMarkerOffset(1.2) {
            + IntakeArmPositionCmd(0.57) // TODO: score position of intake arm
        }
        lineToLinearHeading(Pose2d(30.0, -6.8, Math.toRadians(90.0))) // TODO: high pole coordinates

        UNSTABLE_addTemporalMarkerOffset(0.4) {
            + IntakeWheelsReleaseCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.8) {
            + IntakeArmPositionSaveCmd()
            + IntakeWheelsStopCmd()

            + LiftMoveToPosCmd(endingLiftPos ?: Lift.lowPos.toDouble())
            + TurretMoveToAngleCmd(endingTurretAngle)
        }

        waitSeconds(0.8)
    }


}