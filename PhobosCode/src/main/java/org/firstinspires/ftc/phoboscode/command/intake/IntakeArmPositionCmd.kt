package org.firstinspires.ftc.phoboscode.command.intake

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.IntakeArmSubsystem

open class IntakeArmPositionCmd(val position: Double) : DeltaCommand() {
    val sub = require<IntakeArmSubsystem>()

    override fun run() {
        sub.armServo.position = position
    }
}

class IntakeArmPositionSaveCmd : IntakeArmPositionCmd(0.7)
class IntakeArmPositionMiddleCmd : IntakeArmPositionCmd(0.5)

class IntakeArmPositionIncrementCmd(val increment: () -> Double) : DeltaCommand() {
    val sub = require<IntakeArmSubsystem>()

    constructor(increment: Double) : this({increment})

    override fun run() {
        sub.armServo.position += increment()
    }
}