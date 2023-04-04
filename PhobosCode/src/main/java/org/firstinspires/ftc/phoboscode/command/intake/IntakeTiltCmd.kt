package org.firstinspires.ftc.phoboscode.command.intake

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.IntakeArmSubsystem

open class IntakeTiltCmd(val position: Double, val setDownTilt: Boolean = false) : DeltaCommand() {

    val sub = require<IntakeArmSubsystem>()

    override fun init() {
        if(setDownTilt) {
            sub.downTilt = position
        }
    }

    override fun run() {
        sub.tiltServo.position = position
    }

}

class IntakeZeroTiltCmd(setDownTilt: Boolean = false) : IntakeTiltCmd(0.48, setDownTilt)
class IntakeSaveTiltCmd(setDownTilt: Boolean = false) : IntakeTiltCmd(0.9, setDownTilt)