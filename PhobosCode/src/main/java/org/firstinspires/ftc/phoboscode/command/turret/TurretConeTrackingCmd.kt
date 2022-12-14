package org.firstinspires.ftc.phoboscode.command.turret

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.phoboscode.subsystem.Turret
import org.firstinspires.ftc.phoboscode.subsystem.TurretSubsystem
import org.firstinspires.ftc.phoboscode.vision.ConeTrackingPipeline
import kotlin.math.abs
import kotlin.math.sign

class TurretConeTrackingCmd(
    val pipeline: ConeTrackingPipeline
) : DeltaCommand() {

    val sub = require<TurretSubsystem>()

    private val deltaTimer = ElapsedTime()

    override fun run() {
        sub.trackingController.targetPosition = 0.0

        sub.controller.targetPosition = Range.clip(
            (sub.controller.targetPosition / Turret.ticksPerAngle) - sub.trackingController.update(pipeline.lastErrorFromCenter) * Turret.maxDegreesPerSecond * deltaTimer.seconds(),
            -45.0, 45.0
        ) * Turret.ticksPerAngle

        sub.motor.power = sub.controller.update(sub.motor.currentPosition.toDouble())

        deltaTimer.reset()
    }

}