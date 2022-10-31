package org.firstinspires.ftc.phoboscode.command.intake

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.IntakeWheelsSubsystem

open class IntakeWheelsMoveCmd(val power: Double) : DeltaCommand() {
    val sub = require<IntakeWheelsSubsystem>()

    override fun run() {
        sub.leftServo.power = power
        sub.rightServo.power = power
    }
}

class IntakeWheelsAbsorbCmd : IntakeWheelsMoveCmd(1.0)
class IntakeWheelsReleaseCmd : IntakeWheelsMoveCmd(-1.0)
class IntakeWheelsStopCmd : IntakeWheelsMoveCmd(0.0)
class IntakeWheelsHoldCmd : IntakeWheelsMoveCmd(0.1)