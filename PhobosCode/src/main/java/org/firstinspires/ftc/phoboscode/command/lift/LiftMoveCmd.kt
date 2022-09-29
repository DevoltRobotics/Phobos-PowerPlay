package org.firstinspires.ftc.phoboscode.command.lift

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.subsystem
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem

class LiftMoveCmd(val powerSupplier: () -> Double) : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    constructor(power: Double) : this({power})

    override fun run() {
        sub.leftMotor.power = powerSupplier()
        sub.rightMotor.power = powerSupplier()
    }

}