package org.firstinspires.ftc.phoboscode.command.turret

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.Turret
import org.firstinspires.ftc.phoboscode.subsystem.TurretSubsystem

class TurretMoveToAngleCmd(val angle: Double, val endOnTargetReached: Boolean = false) : DeltaCommand() {

    val sub = require<TurretSubsystem>()

    override fun init() {
        sub.controller.reset()
        sub.controller.targetPosition = Turret.ticksPerAngle * angle
    }

    override fun run() {
        sub.motor.power = sub.controller.update(sub.motor.currentPosition.toDouble())
    }

    override fun isActive() = if(endOnTargetReached) {
        !sub.isOnTarget
    } else true

}