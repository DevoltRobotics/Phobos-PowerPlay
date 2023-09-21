package org.firstinspires.ftc.phoboscode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.command.intake.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToPosCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd
import org.firstinspires.ftc.phoboscode.rr.drive.DriveConstants
import org.firstinspires.ftc.phoboscode.rr.drive.SampleMecanumDrive
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequenceBuilder
import org.firstinspires.ftc.phoboscode.subsystem.Lift
import org.firstinspires.ftc.phoboscode.vision.SleevePattern
import org.firstinspires.ftc.phoboscode.vision.SleevePattern.*
import kotlin.math.roundToInt

abstract class AutonomoAPrueba(
    alliance: Alliance,
    val cycles: Int = 5,
    val centerline: Boolean = true
) : AutonomoBase(alliance) {

    override val startPose = Pose2d(-34.5, -57.5, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
        }

        // prepare for putting preload cone
        UNSTABLE_addTemporalMarkerOffset(0.0) { + prepareForPuttingCone(if(centerline) -80.0 else -43.0, Lift.highPos - 110) } //TODO: altura elevador primer cono

        UNSTABLE_addTemporalMarkerOffset(if(centerline) 1.8 else 1.4) {
            + IntakeArmAndTiltCmd(if(centerline) 0.52 else 0.51, if(centerline) 0.55 else 0.60)
        }

        lineToConstantHeading(Vector2d(if(centerline) -35.9 else -32.9, if(centerline) 3.6 else -2.9), // TODO: Preload cone score position
            SampleMecanumDrive.getVelocityConstraint(
                DriveConstants.MAX_VEL, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH
            ),
            SampleMecanumDrive.getAccelerationConstraint(
                DriveConstants.MAX_ACCEL
            )
        )

        UNSTABLE_addTemporalMarkerOffset(0.001) { + IntakeWheelsReleaseCmd() }
        waitSeconds(0.26)

        var liftHeight = 410.0 // TODO: altura de los rieles

        UNSTABLE_addTemporalMarkerOffset(0.0) {
            +IntakeArmPositionSaveCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.1) {
            +IntakeWheelsStopCmd()

            + LiftMoveToPosCmd(liftHeight + 400) // TODO: first cone grab height
            + TurretMoveToAngleCmd(93.0)
        }

        var grabX = -57.45 // TODO: Grab coordinates
        var grabY = -5.9

        if(centerline) {
            setReversed(true)

            splineToConstantHeading(
                Vector2d(-40.0, grabY), Math.toRadians(180.0),
                SampleMecanumDrive.getVelocityConstraint(
                    DriveConstants.MAX_VEL, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH
                ),
                SampleMecanumDrive.getAccelerationConstraint(
                    DriveConstants.MAX_ACCEL * 0.4
                )
            )
        }

        UNSTABLE_addTemporalMarkerOffset(if(centerline) 0.0 else 1.1) {
            + IntakeArmAndZeroTiltCmd(0.45)
            + IntakeWheelsAbsorbCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(2.0) {
            + IntakeWheelsHoldCmd()
        }

        lineToConstantHeading(Vector2d(grabX, grabY),
            SampleMecanumDrive.getVelocityConstraint(
                DriveConstants.MAX_VEL, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH
            ),
            SampleMecanumDrive.getAccelerationConstraint(
                DriveConstants.MAX_ACCEL * 0.4
            )
        )
        setReversed(false)

        waitSeconds(0.4)

        repeat(cycles - 1) {
            liftHeight -= 140

            putOnHigh(93.0, liftHeight, it + 1)

            UNSTABLE_addTemporalMarkerOffset(0.8) {
                + IntakeWheelsAbsorbCmd()
            }

            val armPosition = if(cycles == 5 && it == cycles - 1) {
                0.33
            } else 0.45

            UNSTABLE_addTemporalMarkerOffset(1.0) {
                + IntakeArmAndZeroTiltCmd(armPosition)
            }

            UNSTABLE_addTemporalMarkerOffset(1.3) {
                + IntakeArmPositionCmd(armPosition - 0.04)
            }

            lineToSplineHeading(Pose2d(grabX, grabY, Math.toRadians(90.0)))

            waitSeconds(0.15)

            //grabX += if(cycles == 5) {
            //    0.08
            //} else {
            //    0.07
            //}
        }

        putOnHigh(endingLiftPos = 0.0, endingTurretAngle = 0.0)

        when(sleevePattern) {
            A -> {
                lineToLinearHeading(Pose2d(-60.0, -6.3, Math.toRadians(90.0)),
                    SampleMecanumDrive.getVelocityConstraint(
                        DriveConstants.MAX_VEL * 3.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH
                    ),
                    SampleMecanumDrive.getAccelerationConstraint(
                        DriveConstants.MAX_ACCEL
                    )
                )
            }
            B -> {
                lineToLinearHeading(Pose2d(-34.5, -7.3, Math.toRadians(90.0)))
            }
            C -> {
                lineToLinearHeading(Pose2d(-11.0, -7.7, Math.toRadians(90.0)))
            }
        }

        waitSeconds(5.0)

        // besito de la suerte
    }.build()

    fun prepareForPuttingCone(turretAngle: Double, liftPos: Int = Lift.highPos) = deltaSequence {
        - LiftMoveToPosCmd(liftPos.toDouble()).dontBlock()

        - waitForSeconds(0.1)

        - TurretMoveToAngleCmd(turretAngle).dontBlock()
    }

    private var putOnHighX = -25.9
    private var elevatorOffset = 5.0

    fun TrajectorySequenceBuilder.putOnHigh(endingTurretAngle: Double, endingLiftPos: Double? = null, cycle: Int = 1) {
        if(cycle == 4) {
            UNSTABLE_addTemporalMarkerOffset(-0.1) {
                + LiftMoveToPosCmd(Lift.lowPos + 100.0)
                + IntakeWheelsHoldCmd()
            }

            UNSTABLE_addTemporalMarkerOffset(0.3) {
                + IntakeArmPositionSaveCmd()
            }
        } else {
            UNSTABLE_addTemporalMarkerOffset(0.0) {
                + IntakeArmPositionSaveCmd()
                + IntakeWheelsHoldCmd()
            }
        }

        UNSTABLE_addTemporalMarkerOffset(0.2) { // TODO: tiempo para que se mueva la torreta
            +prepareForPuttingCone(
                -16.0 /*11.5*/,
                (Lift.highPos + elevatorOffset).roundToInt()
            ) // TODO: Angulo de la torreta para poner
        }

        UNSTABLE_addTemporalMarkerOffset(1.3) {
            + IntakeArmAndTiltCmd(0.51, 0.6) // TODO: score position of intake arm
        }
        lineToConstantHeading(Vector2d(putOnHighX, -7.4)) // TODO: high pole coordinates

        UNSTABLE_addTemporalMarkerOffset(0.00008) {
            + IntakeWheelsReleaseCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.35) {
            + IntakeArmPositionSaveCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.52) {
            + IntakeWheelsStopCmd()

            + LiftMoveToPosCmd(endingLiftPos ?: Lift.lowPos.toDouble())
            + TurretMoveToAngleCmd(endingTurretAngle)
        }

        waitSeconds(0.37)

        putOnHighX += 0.05
        elevatorOffset += 7
    }

}