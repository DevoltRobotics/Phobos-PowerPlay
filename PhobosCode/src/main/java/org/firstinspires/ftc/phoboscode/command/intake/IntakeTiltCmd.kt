package org.firstinspires.ftc.phoboscode.command.intake

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.IntakeArmSubsystem

open class IntakeTiltCmd(val position: Double) : DeltaCommand() {

    val sub = require<IntakeArmSubsystem>()

    override fun run() {
        sub.tiltServo.position = position
    }

}

class IntakeZeroTiltCmd : IntakeTiltCmd(0.5)