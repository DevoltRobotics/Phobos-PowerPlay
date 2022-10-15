package org.firstinspires.ftc.phoboscode.command.lift

import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.subsystem
import org.firstinspires.ftc.phoboscode.subsystem.Lift
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem

open class LiftMoveToPosCmd(val position: Double) : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    override fun init() {
        sub.controller.reset()
        sub.controller.targetPosition = position
    }

    override fun run() {
        sub.power = sub.controller.update(sub.leftMotor.currentPosition.toDouble())
    }

}

class LiftMoveToHighCmd : LiftMoveToPosCmd(Lift.highPos.toDouble())
class LiftMoveToMidCmd : LiftMoveToPosCmd(Lift.midPos.toDouble())
class LiftMoveToLowCmd : LiftMoveToPosCmd(Lift.lowPos.toDouble())