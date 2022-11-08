package org.firstinspires.ftc.phoboscode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.github.serivesmejia.deltacommander.endRightAway
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.command.intake.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveDownCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToHighCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToPosCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd
import org.firstinspires.ftc.phoboscode.rr.drive.DriveConstants
import org.firstinspires.ftc.phoboscode.rr.drive.SampleMecanumDrive
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequenceBuilder
import org.firstinspires.ftc.phoboscode.vision.SleevePattern
import org.firstinspires.ftc.phoboscode.vision.SleevePattern.*

@Autonomous(name = "R - Full Izquierda", group = "rojo")
abstract class AutonomoA(
    val cycles: Int = 2
) : AutonomoBase() {

    override val startPose = Pose2d(-35.0, -58.0, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
        }

        // prepare for putting preload cone
        UNSTABLE_addTemporalMarkerOffset(1.0) { + prepareForPuttingCone(-90.0) }
        lineToConstantHeading(Vector2d(-34.5, 5.0))

        // put it
        UNSTABLE_addTemporalMarkerOffset(0.4) { + IntakeArmPositionMiddleCmd() }
        UNSTABLE_addTemporalMarkerOffset(0.9) { + IntakeWheelsReleaseCmd() }
        waitSeconds(0.5)
        lineToConstantHeading(Vector2d(-37.5, 5.0))
        waitSeconds(0.8)

        // save turret and prepare for cycling or parking
        UNSTABLE_addTemporalMarkerOffset(-0.2) {
            + saveTurret()
            //drive.relocalizeWithIMU()
        }
        waitSeconds(0.5)

        // just park here when we won`t be doing any cycles
        if(cycles == 0) {
            park(sleevePattern)

            return@apply
        }

        setReversed(true)
        lineToSplineHeading(Pose2d(-35.0, -1.0, Math.toRadians(180.0)))

        grabHere(330.0)
        splineToConstantHeading(Vector2d(-54.0, -8.5), Math.toRadians(180.0))
        setReversed(false)

        UNSTABLE_addTemporalMarkerOffset(0.5) { + IntakeArmPositionCmd(0.3) }
        lineToConstantHeading(Vector2d(-57.3, -8.5), SampleMecanumDrive.getVelocityConstraint(20.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH), SampleMecanumDrive.getAccelerationConstraint(60.0))
        waitSeconds(1.0)

        repeat(2) {
            UNSTABLE_addTemporalMarkerOffset(0.0) {
                + IntakeArmPositionSaveCmd()
            }
            UNSTABLE_addTemporalMarkerOffset(1.0) {
                + deltaSequence {
                    - LiftMoveToHighCmd().dontBlock()
                    - waitForSeconds(0.3)
                    - TurretMoveToAngleCmd(-90.0)
                }
            }
            lineToLinearHeading(Pose2d(-22.0, -8.5, Math.toRadians(180.0)))
            UNSTABLE_addTemporalMarkerOffset(0.4) {
                + IntakeArmPositionMiddleCmd()
            }
            UNSTABLE_addTemporalMarkerOffset(0.9) {
                + IntakeWheelsReleaseCmd()
            }
            UNSTABLE_addTemporalMarkerOffset(1.8) {
                + saveTurret()
                drive.relocalizeWithIMU()
            }
            waitSeconds(2.4)

            grabHere(300.0)
            lineToLinearHeading(Pose2d(-54.0, -8.5, Math.toRadians(180.0)))
            UNSTABLE_addTemporalMarkerOffset(0.3) {
                + IntakeArmPositionCmd(0.35)
            }
            lineToConstantHeading(Vector2d(-58.3, -8.5), SampleMecanumDrive.getVelocityConstraint(20.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH), SampleMecanumDrive.getAccelerationConstraint(60.0))
            waitSeconds(2.3)
        }

        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + LiftMoveToPosCmd(0.0)
            + TurretMoveToAngleCmd(-0.0)
            + IntakeArmPositionCmd(0.7)
        }
        lineToConstantHeading(Vector2d(-22.0, -8.0))
        waitSeconds(2.0)

        park(sleevePattern)
    }.build()

    fun grabCone(liftPos: Double?) = deltaSequence {
        - LiftMoveToPosCmd(liftPos ?: 0.0).dontBlock()
        - IntakeWheelsAbsorbCmd().dontBlock()

        - IntakeTiltCmd(0.7).dontBlock()
        - IntakeArmPositionCmd(0.0).dontBlock()
    }

    fun prepareForPuttingCone(turretAngle: Double) = deltaSequence {
        - TurretMoveToAngleCmd(turretAngle).dontBlock()

        - waitForSeconds(0.2)

        - LiftMoveToHighCmd()
    }

    fun saveTurret() = deltaSequence {
        - IntakeArmPositionSaveCmd().dontBlock()
        - waitForSeconds(0.3)
        - IntakeSaveTiltCmd().dontBlock()
        - LiftMoveDownCmd().dontBlock()

        - waitForSeconds(0.6)

        - TurretMoveToAngleCmd(0.0)
    }

    fun TrajectorySequenceBuilder.grabHere(liftPos: Double) {
        UNSTABLE_addTemporalMarkerOffset(-1.2) {
            + deltaSequence {
                - waitForSeconds(0.8)
                - LiftMoveToPosCmd(liftPos)
                - IntakeArmPositionSaveCmd().endRightAway()
                - waitForSeconds(0.3)
                - IntakeTiltCmd(0.9).endRightAway()
            }
        }
        UNSTABLE_addTemporalMarkerOffset(0.7) {
            + IntakeWheelsAbsorbCmd()
        }
    }

    fun TrajectorySequenceBuilder.park(sleevePattern: SleevePattern) {
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
    }

}