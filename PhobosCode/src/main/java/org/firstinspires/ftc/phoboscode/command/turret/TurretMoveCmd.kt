package org.firstinspires.ftc.phoboscode.command.turret

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.subsystem
import org.firstinspires.ftc.phoboscode.subsystem.TurretSubsystem

class TurretMoveCmd(val powerSupplier: () -> Double) : DeltaCommand() {

    val sub = require<TurretSubsystem>()

    constructor(power: Double) : this({power})

    override fun run() {
        sub.motor.power = powerSupplier()
    }

    override fun end(interrupted: Boolean) {
        sub.motor.power = 0.0
    }

}