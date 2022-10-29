package org.firstinspires.ftc.phoboscode.command.intake

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.IntakeArmSubsystem

class IntakeArmAndTiltZeroCmd() : DeltaCommand() {
    val sub = require<IntakeArmSubsystem>()

    override fun run() {
        sub.armServo.position = 0.5
        sub.tiltServo.position = 0.5
    }
}