package org.firstinspires.ftc.phoboscode.command.intake

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.deltaScheduler
import org.firstinspires.ftc.phoboscode.subsystem.IntakeSubsystem

open class IntakeArmPositionCmd(val position: Double) : DeltaCommand() {
    val sub = require<IntakeSubsystem>()

    override fun run() {
        sub.armServo.position = position
    }
}

class IntakeArmPositionMiddleCmd : IntakeArmPositionCmd(0.5)

class IntakeArmPositionIncrementCmd(val increment: () -> Double) : DeltaCommand() {
    val sub = require<IntakeSubsystem>()

    constructor(increment: Double) : this({increment})

    override fun run() {
        sub.armServo.position += increment()
    }
}