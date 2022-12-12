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

abstract class AutonomoA(
    alliance: Alliance,
    val cycles: Int = 3
) : AutonomoBase(alliance) {

    override val startPose = Pose2d(-35.0, -58.0, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
        }

        // prepare for putting preload cone
        UNSTABLE_addTemporalMarkerOffset(0.8) { + prepareForPuttingCone(-90.0) }
        lineToConstantHeading(Vector2d(-35.5, 6.5)) // TODO: Preload cone score position

        UNSTABLE_addTemporalMarkerOffset(0.0) { + IntakeArmPositionCmd(0.56) }
        UNSTABLE_addTemporalMarkerOffset(0.5) { + IntakeWheelsReleaseCmd() }
        waitSeconds(0.9)

        var liftHeight = 380.0

        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
        }
        UNSTABLE_addTemporalMarkerOffset(0.1) {
            + IntakeSaveTiltCmd()
            + IntakeWheelsStopCmd()

            + LiftMoveToPosCmd(liftHeight)
            + TurretMoveToAngleCmd(90.0)
        }

        waitSeconds(0.5)

        // just park here when we won`t be doing any cycles
        if(cycles == 0) {
            // park
            when(sleevePattern) {
                A -> {
                    lineToSplineHeading(Pose2d(-35.0, -33.0, Math.toRadians(0.0)))

                    setReversed(true)
                    splineToConstantHeading(Vector2d(-58.0, -35.0), Math.toRadians(180.0))
                    setReversed(false)
                }
                B -> {
                    lineToLinearHeading(Pose2d(-35.0, -35.0, Math.toRadians(0.0)))
                }
                C -> {
                    lineToSplineHeading(Pose2d(-35.0, -33.0, Math.toRadians(0.0)))
                    lineToConstantHeading(Vector2d(-11.5, -35.0))

                    turn(Math.toRadians(-90.0))
                }
            }

            return@apply
        }

        val grabX = -55.5 // TODO: Grab coordinates
        var grabY = -7.3

        lineToSplineHeading(Pose2d(-37.0, grabY, Math.toRadians(90.0)))

        UNSTABLE_addTemporalMarkerOffset(0.7) {
            + deltaSequence {
                - IntakeArmPositionCmd(0.47).dontBlock()
                - waitForSeconds(0.3)
                - IntakeTiltCmd(0.58).dontBlock()
            }
        }

        UNSTABLE_addTemporalMarkerOffset(0.9) {
            + IntakeWheelsAbsorbCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(1.0) {
            + IntakeArmPositionCmd(0.44)
        }

        lineToSplineHeading(Pose2d(grabX, grabY, Math.toRadians(90.0)))

        waitSeconds(0.6)

        repeat(cycles - 1) {
            liftHeight -= 50

            putOnHigh(90.0, liftHeight)

            UNSTABLE_addTemporalMarkerOffset(0.8) {
                + IntakeWheelsAbsorbCmd()
            }

            UNSTABLE_addTemporalMarkerOffset(1.0) {
                + deltaSequence {
                    - IntakeArmPositionCmd(0.46).dontBlock()
                    - waitForSeconds(0.3)
                    - IntakeTiltCmd(0.53).dontBlock()
                }
            }

            UNSTABLE_addTemporalMarkerOffset(1.4) {
                + IntakeArmPositionCmd(0.44)
            }

            lineToSplineHeading(Pose2d(grabX, grabY, Math.toRadians(90.0)))

            waitSeconds(0.5)

            grabY -= 0.1
        }

        putOnHigh(endingLiftPos = 0.0, endingTurretAngle = 0.0)

        when(sleevePattern) {
            A -> {
                lineToLinearHeading(Pose2d(-56.0, -7.3, Math.toRadians(90.0)))
            }
            B -> {
                lineToLinearHeading(Pose2d(-35.0, -7.3, Math.toRadians(90.0)))
            }
            C -> { 
                lineToLinearHeading(Pose2d(-14.0, -7.3, Math.toRadians(90.0)))
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
        UNSTABLE_addTemporalMarkerOffset(0.3) {
            + prepareForPuttingCone(0.0, Lift.highPos + 40)
        }

        UNSTABLE_addTemporalMarkerOffset(1.5) {
            + IntakeArmPositionCmd(0.57)
        }
        lineToLinearHeading(Pose2d(-23.5, -6.3, Math.toRadians(90.0))) //TODO: tubo high

        UNSTABLE_addTemporalMarkerOffset(0.5) {
            + IntakeWheelsReleaseCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(1.0) {
            + IntakeArmPositionSaveCmd()
        }
        UNSTABLE_addTemporalMarkerOffset(1.2) {
            + IntakeSaveTiltCmd()
            + IntakeWheelsStopCmd()

            + LiftMoveToPosCmd(endingLiftPos ?: Lift.lowPos.toDouble())
            + TurretMoveToAngleCmd(endingTurretAngle)
        }

        waitSeconds(1.2)
    }

}