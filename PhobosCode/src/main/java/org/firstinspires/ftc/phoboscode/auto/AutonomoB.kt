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
    val cycles: Int = 5
) : AutonomoBase(alliance) {

    override val startPose = Pose2d(35.0, -58.0, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) =
        drive.trajectorySequenceBuilder(startPose).apply {
            UNSTABLE_addTemporalMarkerOffset(0.0) {
                +IntakeArmPositionSaveCmd()
            }

            // prepare for putting preload cone
            UNSTABLE_addTemporalMarkerOffset(0.5) {
                + prepareForPuttingCone(
                    40.0,
                    Lift.highPos - 80
                )
            }
            UNSTABLE_addTemporalMarkerOffset(1.2) {
                + TurretMoveToAngleCmd(88.0)
            }
            UNSTABLE_addTemporalMarkerOffset(1.6) {
                + IntakeArmAndTiltCmd(0.6, 0.48)
            }
            lineToConstantHeading(
                Vector2d(34.0, 3.9), // TODO: Preload cone score position
                SampleMecanumDrive.getVelocityConstraint(
                    DriveConstants.MAX_VEL * 1.15,
                    DriveConstants.MAX_ANG_VEL,
                    DriveConstants.TRACK_WIDTH
                ),
                SampleMecanumDrive.getAccelerationConstraint(
                    DriveConstants.MAX_ACCEL * 1.2
                )
            )

            UNSTABLE_addTemporalMarkerOffset(0.003) { +IntakeWheelsReleaseCmd() }
            waitSeconds(0.26)

            var liftHeight = 485.0 // TODO: altura de los rieles

            // just park here when we won`t be doing any cycles
            if (cycles == 0) {

                UNSTABLE_addTemporalMarkerOffset(1.0) {
                    +IntakeArmPositionSaveCmd()
                    +IntakeWheelsStopCmd()

                    +LiftMoveToPosCmd(0.0)
                    +TurretMoveToAngleCmd(0.0)
                }

                // park
                when (sleevePattern) {
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
                        lineToConstantHeading(Vector2d(-11.5, -35.0))

                        turn(Math.toRadians(90.0))
                    }
                }

                return@apply
            }

            UNSTABLE_addTemporalMarkerOffset(0.1) {
                +IntakeArmPositionSaveCmd()
                +IntakeWheelsStopCmd()

                + LiftMoveToPosCmd(liftHeight + 435) // TODO: first cone grab height
                + TurretMoveToAngleCmd(-90.0)
            }

            var grabX = 57.0 // TODO: Grab coordinates
            var grabY = -6.1

            setReversed(true)
            splineToConstantHeading(Vector2d(40.0, grabY), Math.toRadians(0.0))

            UNSTABLE_addTemporalMarkerOffset(0.2) {
                + IntakeArmAndZeroTiltCmd(0.43)
                + IntakeWheelsAbsorbCmd()
            }

            lineToConstantHeading(Vector2d(grabX, grabY))
            setReversed(false)

            waitSeconds(0.75)

            repeat(cycles - 1) {
                liftHeight -= 130

                putOnHigh(-95.0, liftHeight)

                UNSTABLE_addTemporalMarkerOffset(0.8) {
                    +IntakeWheelsAbsorbCmd()
                }

                val armPosition = if (cycles == 5 && it == cycles - 1) {
                    0.41
                } else 0.45

                UNSTABLE_addTemporalMarkerOffset(1.0) {
                    +IntakeArmAndZeroTiltCmd(armPosition)
                }

                UNSTABLE_addTemporalMarkerOffset(1.3) {
                    +IntakeArmPositionCmd(armPosition - 0.04)
                }

                lineToSplineHeading(Pose2d(grabX, grabY, Math.toRadians(90.0)))

                waitSeconds(0.3)

                grabY -= 0.02
                grabX += 0.06
            }

            putOnHigh(endingLiftPos = 0.0, endingTurretAngle = 0.0)

            when (sleevePattern) {
                A -> {
                    lineToLinearHeading(Pose2d(11.0, -7.7, Math.toRadians(90.0)))
                }
                B -> {
                    lineToLinearHeading(Pose2d(36.0, -7.3, Math.toRadians(90.0)))
                }
                C -> {
                    lineToLinearHeading(Pose2d(64.0, -7.3, Math.toRadians(90.0)),
                        SampleMecanumDrive.getVelocityConstraint(
                            DriveConstants.MAX_VEL * 1.5, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH
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
        -LiftMoveToPosCmd(liftPos.toDouble()).dontBlock()

        -waitForSeconds(0.1)

        -TurretMoveToAngleCmd(turretAngle).dontBlock()
    }

    private var putOnHighX = 26.8
    private var elevatorOffset = 5.0

    fun TrajectorySequenceBuilder.putOnHigh(
        endingTurretAngle: Double,
        endingLiftPos: Double? = null
    ) {
        UNSTABLE_addTemporalMarkerOffset(0.0) {
            +IntakeArmPositionSaveCmd()
            +IntakeWheelsHoldCmd()
        }
        UNSTABLE_addTemporalMarkerOffset(0.2) { // TODO: tiempo para que se mueva la torreta
            +prepareForPuttingCone(
                35.5 /*11.5*/,
                (Lift.highPos + elevatorOffset).roundToInt()
            ) // TODO: Angulo de la torreta para poner
        }
        UNSTABLE_addTemporalMarkerOffset(1.35) {
            //+ LiftMoveToPosCmd((Lift.highPos + elevatorOffset))
        }

        UNSTABLE_addTemporalMarkerOffset(1.3) {
            +IntakeArmAndTiltCmd(0.53, 0.45) // TODO: score position of intake arm
        }
        lineToConstantHeading(Vector2d(putOnHighX, -6.2)) // TODO: high pole coordinates

        UNSTABLE_addTemporalMarkerOffset(0.0005) {
            +IntakeWheelsReleaseCmd()
        }

        UNSTABLE_addTemporalMarkerOffset(0.25) {
            +IntakeArmPositionSaveCmd()

            +LiftMoveToPosCmd(endingLiftPos ?: Lift.lowPos.toDouble())
            +TurretMoveToAngleCmd(endingTurretAngle)
        }

        UNSTABLE_addTemporalMarkerOffset(0.3) {
            +IntakeWheelsStopCmd()
        }

        waitSeconds(0.27)

        putOnHighX -= 0.09
        elevatorOffset += 7
    }

}