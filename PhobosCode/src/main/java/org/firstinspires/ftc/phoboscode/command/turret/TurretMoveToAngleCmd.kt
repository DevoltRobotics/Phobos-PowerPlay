package org.firstinspires.ftc.phoboscode.command.turret

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.deltaScheduler
import org.firstinspires.ftc.phoboscode.subsystem.Turret
import org.firstinspires.ftc.phoboscode.subsystem.TurretSubsystem

open class TurretMoveToAngleCmd(var angle: Double, val endOnTargetReached: Boolean = false) : DeltaCommand() {

    val sub = require<TurretSubsystem>()

    init {
        try {
            throw Exception()
        } catch (ex: Exception) {
            println("target angle: $angle")
            ex.printStackTrace()
        }
    }

    override fun init() {
        sub.controller.reset()
        sub.controller.targetPosition = Turret.ticksPerAngle * angle
    }

    override fun run() {
        sub.controller.targetPosition = Turret.ticksPerAngle * angle
        sub.motor.power = sub.controller.update(sub.motor.currentPosition.toDouble()) * 0.7

        if(endOnTargetReached && !sub.isOnTarget) {
            deltaScheduler.end(this)
        }
    }


    override fun end(interrupted: Boolean) {
        sub.motor.power = 0.0
    }

}