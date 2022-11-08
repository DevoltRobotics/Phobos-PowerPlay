package org.firstinspires.ftc.phoboscode.command.lift

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.subsystem
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.phoboscode.subsystem.Lift
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem

class LiftMoveCmd(val powerSupplier: () -> Double) : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    constructor(power: Double) : this({power})

    override fun run() {
        sub.power = powerSupplier()
    }

}

class LiftMoveDownCmd : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    val timer = ElapsedTime()

    override fun init() {
        timer.reset()
    }

    override fun run() {
        if(!sub.bottomLimitSensor.isPressed) {
            val perc = Range.clip(1.2 - (timer.seconds() / 1.1), 0.0, 1.0)

            sub.leftMotor.power = Lift.moveDownPower * perc
            sub.rightMotor.power = Lift.moveDownPower * perc
        } else {
            sub.power = 0.0
        }
    }

}