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
    val cycles: Int = 2
) : AutonomoBase(alliance) {

    override val startPose = Pose2d(-35.0, -58.0, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
        }

        // prepare for putting preload cone
        UNSTABLE_addTemporalMarkerOffset(1.0) { + prepareForPuttingCone(-90.0) }
        lineToConstantHeading(Vector2d(-35.0, 5.5))

        // put it
        UNSTABLE_addTemporalMarkerOffset(0.5) { + IntakeArmPositionMiddleCmd() }
        UNSTABLE_addTemporalMarkerOffset(0.9) { + IntakeWheelsReleaseCmd() }
        lineToConstantHeading(Vector2d(-38.5, 5.5))
        waitSeconds(1.2)

        var liftHeight = 300.0

        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + saveTurret(liftHeight)
        }
        waitSeconds(0.9)

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

        UNSTABLE_addTemporalMarkerOffset(2.4) {
            + IntakeWheelsAbsorbCmd()

            drive.relocalizeWithIMU()
        }

        lineToConstantHeading(Vector2d(-35.0, -7.5))
        turn(Math.toRadians(90.0), Math.toRadians(180.0), Math.toRadians(180.0))

        UNSTABLE_addTemporalMarkerOffset(0.5) {
            + deltaSequence {
                - IntakeArmPositionCmd(0.47).dontBlock()
                - waitForSeconds(0.3)
                - IntakeTiltCmd(0.52).dontBlock()
            }
        }

        lineToLinearHeading(Pose2d(-59.0, -7.5, Math.toRadians(180.0)))
        waitSeconds(1.0)

        repeat(cycles - 1) {
            liftHeight -= 40

            putOnHigh(liftHeight)

            UNSTABLE_addTemporalMarkerOffset(1.4) {
                + IntakeWheelsAbsorbCmd()
            }

            UNSTABLE_addTemporalMarkerOffset(1.8) {
                + deltaSequence {
                    - IntakeArmPositionCmd(0.5).dontBlock()
                    - waitForSeconds(0.3)
                    - IntakeTiltCmd(0.52).dontBlock()
                }
            }

            lineToLinearHeading(Pose2d(-55.0, -7.9, Math.toRadians(180.0)))

            lineToLinearHeading(Pose2d(-59.0, -7.9, Math.toRadians(180.0)), SampleMecanumDrive.getVelocityConstraint(20.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH), SampleMecanumDrive.getAccelerationConstraint(60.0))
            waitSeconds(0.8)
        }

        putOnHigh()

        when(sleevePattern) {
            A -> {
                lineToLinearHeading(Pose2d(-56.0, -10.0, Math.toRadians(180.0)))
            }
            B -> {
                lineToLinearHeading(Pose2d(-35.0, -10.0, Math.toRadians(180.0)))
            }
            C -> { 
                lineToLinearHeading(Pose2d(-12.0, -10.0, Math.toRadians(270.0)))
            }
        }

        waitSeconds(2.0)
    }.build()

    fun prepareForPuttingCone(turretAngle: Double, liftPos: Int = Lift.highPos) = deltaSequence {
        - TurretMoveToAngleCmd(turretAngle).dontBlock()

        - waitForSeconds(0.1)

        - LiftMoveToPosCmd(liftPos.toDouble()).dontBlock()
    }

    fun saveTurret(liftPos: Double? = null) = deltaSequence {
        - IntakeArmPositionSaveCmd().dontBlock()
        - waitForSeconds(0.1)
        - IntakeSaveTiltCmd().dontBlock()
        - IntakeWheelsStopCmd().dontBlock()

        if(liftPos == null) {
            - LiftMoveDownCmd().dontBlock()
        } else {
            - LiftMoveToPosCmd(liftPos)
        }

        - waitForSeconds(0.2)

        - TurretMoveToAngleCmd(0.0)
    }

    fun TrajectorySequenceBuilder.putOnHigh(endingLiftPos: Double? = null) {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
            + IntakeWheelsHoldCmd()
        }
        UNSTABLE_addTemporalMarkerOffset(0.5) {
            + prepareForPuttingCone(-90.0, Lift.highPos + 40)
        }

        UNSTABLE_addTemporalMarkerOffset(1.8) {
            + IntakeArmPositionMiddleCmd()
        }
        lineToLinearHeading(Pose2d(-25.8, -8.5, Math.toRadians(180.0)))

        UNSTABLE_addTemporalMarkerOffset(1.0) {
            + IntakeWheelsReleaseCmd()
        }
        UNSTABLE_addTemporalMarkerOffset(1.4) {
            + saveTurret(endingLiftPos)
            drive.relocalizeWithIMU()
        }
        waitSeconds(1.6)
    }

}