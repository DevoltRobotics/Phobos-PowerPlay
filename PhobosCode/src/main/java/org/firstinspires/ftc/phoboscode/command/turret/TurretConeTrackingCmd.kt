package org.firstinspires.ftc.phoboscode.command.turret

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.phoboscode.subsystem.Turret
import org.firstinspires.ftc.phoboscode.subsystem.TurretSubsystem
import org.firstinspires.ftc.phoboscode.vision.ConeTrackingPipeline

class TurretConeTrackingCmd(
    val pipeline: ConeTrackingPipeline
) : DeltaCommand() {

    val sub = require<TurretSubsystem>()

    private var lastPower = 0.0

    override fun run() {
        val angle = sub.motor.currentPosition / Turret.ticksPerAngle
        val power = sub.trackingController.update(pipeline.lastErrorFromCenter) * 0.7
        val deltaPower = power - lastPower

        sub.trackingController.targetPosition = 0.0

        sub.motor.power = if((angle >= -45.0 || deltaPower >= 0.1) || (angle <= 45.0 || deltaPower <= -0.1)) {
            power
        } else {
            0.0
        }

        lastPower = power
    }

}