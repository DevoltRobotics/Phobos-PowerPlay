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

abstract class AutonomoB(
    alliance: Alliance,
    val cycles: Int = 5,
    val centerline: Boolean = false
) : AutonomoBase(alliance) {

    override val startPose = Pose2d(35.0, -57.5, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
        }

        // prepare for putting preload cone
        //TODO: altura elevador primer cono
        UNSTABLE_addTemporalMarkerOffset(0.0) { + prepareForPuttingCone(if(centerline) 80.0 else 46.0, Lift.highPos - 110) }

        UNSTABLE_addTemporalMarkerOffset(if(centerline) 1.8 else 1.4) {
            + IntakeArmAndTiltCmd(if(centerline) 0.52 else 0.49, if(centerline) 0.45 else 0.4)
        }
        lineToConstantHeading(Vector2d(if(centerline) 35.9 else 32.5, if(centerline) 3.6 else -2.5), // TODO: Preload cone score position
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
            + TurretMoveToAngleCmd(-90.0)
        }

        var grabX = 57.65 // TODO: Grab coordinates
        var grabY = -6.1

        if(centerline) {
            setReversed(true)
            splineToConstantHeading(
                Vector2d(40.0, grabY), Math.toRadians(0.0),
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

        UNSTABLE_addTemporalMarkerOffset(1.8) {
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
            liftHeight -= 130

            putOnHigh(-90.0, liftHeight)

            UNSTABLE_addTemporalMarkerOffset(0.8) {
                + IntakeWheelsAbsorbCmd()
            }

            val armPosition = if(cycles == 5 && it == cycles - 1) {
                0.41
            } else 0.45

            UNSTABLE_addTemporalMarkerOffset(1.0) {
                + IntakeArmAndZeroTiltCmd(armPosition)
            }

            UNSTABLE_addTemporalMarkerOffset(1.3) {
                + IntakeArmPositionCmd(armPosition - 0.04)
            }

            lineToSplineHeading(Pose2d(grabX, grabY, Math.toRadians(90.0)))

            waitSeconds(0.1)

            grabY -= 0.02
        }

        putOnHigh(endingLiftPos = 0.0, endingTurretAngle = 0.0)

        when(sleevePattern) {
            A -> {
                lineToLinearHeading(Pose2d(11.0, -7.7, Math.toRadians(90.0)))
            }
            B -> {
                lineToLinearHeading(Pose2d(34.5, -7.3, Math.toRadians(90.0)))
            }
            C -> {
                lineToLinearHeading(Pose2d(60.0, -7.3, Math.toRadians(90.0)),
                    SampleMecanumDrive.getVelocityConstraint(
                        DriveConstants.MAX_VEL * 2.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH
                    ),
                    SampleMecanumDrive.getAccelerationConstraint(
                        DriveConstants.MAX_ACCEL * 2.0
                    )
                )
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

    private var putOnHighX = 25.0
    private var elevatorOffset = 5.0

    fun TrajectorySequenceBuilder.putOnHigh(endingTurretAngle: Double, endingLiftPos: Double? = null) {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            + IntakeArmPositionSaveCmd()
            + IntakeWheelsHoldCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.2) { // TODO: tiempo para que se mueva la torreta
            +prepareForPuttingCone(
                13.0 /*11.5*/,
                (Lift.highPos + elevatorOffset).roundToInt()
            ) // TODO: Angulo de la torreta para poner
        }

        UNSTABLE_addTemporalMarkerOffset(1.3) {
            + IntakeArmAndTiltCmd(0.51, 0.43) // TODO: score position of intake arm
        }
        lineToConstantHeading(Vector2d(putOnHighX, -6.9)) // TODO: high pole coordinates

        UNSTABLE_addTemporalMarkerOffset(0.00008) {
            + IntakeWheelsReleaseCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.26) {
            + IntakeArmPositionSaveCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.4) {
            + IntakeWheelsStopCmd()

            + LiftMoveToPosCmd(endingLiftPos ?: Lift.lowPos.toDouble())
            + TurretMoveToAngleCmd(endingTurretAngle)
        }

        waitSeconds(0.37)

        putOnHighX += 0.05
        elevatorOffset += 7
    }

}