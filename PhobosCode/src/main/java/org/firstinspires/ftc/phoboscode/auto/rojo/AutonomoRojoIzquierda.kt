package org.firstinspires.ftc.phoboscode.auto.rojo

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.auto.AutonomoBase
import org.firstinspires.ftc.phoboscode.command.intake.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToHighCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToPosCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.phoboscode.vision.SleevePattern

@Autonomous(name = "R - Full Izquierda", group = "rojo")
class AutonomoRojoIzquierda : AutonomoBase() {

    override val startPose = Pose2d(-35.0, -58.0, Math.toRadians(90.0))

    override fun sequence(sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
        UNSTABLE_addDisplacementMarkerOffset(0.0) { + prepareForPuttingCone(-90.0) }
        lineToConstantHeading(Vector2d(-35.0, 1.0))
        
        UNSTABLE_addDisplacementMarkerOffset(0.0) { + putCone() }
        waitSeconds(4.0)

        lineToConstantHeading(Vector2d(-35.0, -11.0))

        splineToSplineHeading(Pose2d(-52.0, -11.0, Math.toRadians(180.0)), Math.toRadians(178.0))
        //UNSTABLE_addTemporalMarkerOffset(0.0) { + saveTurret() }

        waitSeconds(2.0)

        repeat(3) {
            lineToConstantHeading(Vector2d(-24.0, -11.5))
            waitSeconds(2.0)
            lineToConstantHeading(Vector2d(-54.0, -12.5))
            waitSeconds(2.0)
        }

        lineToConstantHeading(Vector2d(-24.0, -11.5))
        waitSeconds(2.0)

        when(sleevePattern) {
            SleevePattern.A -> lineTo(Vector2d(-57.0, -11.5))
            SleevePattern.B -> lineToSplineHeading(Pose2d(-35.0, -11.5, Math.toRadians(270.0)))
            SleevePattern.C -> lineToSplineHeading(Pose2d(-12.0, -11.5, Math.toRadians(270.0)))
        }
    }.build()

    fun grabCone(liftPos: Double?) = deltaSequence {
        - LiftMoveToPosCmd(liftPos ?: 0.0).dontBlock()
        - IntakeWheelsAbsorbCmd().dontBlock()

        - IntakeTiltCmd(0.7).dontBlock()
        - IntakeArmPositionCmd(0.0).dontBlock()
    }

    fun prepareForPuttingCone(turretAngle: Double) = deltaSequence {
        - LiftMoveToHighCmd().dontBlock()
        - IntakeArmPositionCmd(0.6).dontBlock()

        - TurretMoveToAngleCmd(turretAngle)
    }

    fun putCone() = deltaSequence {
        - IntakeArmPositionMiddleCmd().dontBlock()
        - waitForSeconds(0.5)
        - IntakeWheelsReleaseCmd()
    }

    fun saveTurret(liftPos: Double? = null) = deltaSequence {
        - LiftMoveToPosCmd(liftPos ?: 0.0).dontBlock()
        - IntakeArmPositionCmd(1.0).dontBlock()

        - TurretMoveToAngleCmd(0.0, endOnTargetReached = true)
    }

}