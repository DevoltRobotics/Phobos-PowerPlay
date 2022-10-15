package org.firstinspires.ftc.phoboscode.command.intake

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.IntakeSubsystem

open class IntakeTiltCmd(val position: Double) : DeltaCommand() {

    val sub = require<IntakeSubsystem>()

    override fun run() {
        sub.tiltServo.position = position
    }

}

class IntakeZeroTiltCmd : IntakeTiltCmd(0.5)