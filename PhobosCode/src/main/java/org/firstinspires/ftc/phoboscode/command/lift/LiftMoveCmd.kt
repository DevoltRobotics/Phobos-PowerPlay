package org.firstinspires.ftc.phoboscode.command.lift

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.subsystem
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem

class LiftMoveCmd(val powerSupplier: () -> Double) : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    constructor(power: Double) : this({power})

    override fun run() {
        val power = powerSupplier()

        if(sub.touchSensor.isPressed && power < 0) {
            sub.power = 0.0
            return
        }

        sub.power = power
    }

}