package org.firstinspires.ftc.phoboscode.command.lift

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import org.firstinspires.ftc.phoboscode.subsystem.Lift
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem

open class LiftMoveToPosCmd(val position: Double, val stopOnTarget: Boolean = false) : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    override fun init() {
        sub.controller.reset()
        sub.controller.targetPosition = position
    }

    override fun run() {
        sub.power = sub.controller.update(sub.leftMotor.currentPosition.toDouble())

        if(stopOnTarget && sub.controller.lastError > 15) {
            deltaScheduler.end(this)
        }
    }

    override fun end(interrupted: Boolean) {
        sub.power = 0.0
    }
}

class LiftMoveToHighCmd(stopOnTarget: Boolean = true) : LiftMoveToPosCmd(Lift.highPos.toDouble(), stopOnTarget)
class LiftMoveToMidCmd(stopOnTarget: Boolean = true) : LiftMoveToPosCmd(Lift.midPos.toDouble(), stopOnTarget)
class LiftMoveToLowCmd(stopOnTarget: Boolean = true) : LiftMoveToPosCmd(Lift.lowPos.toDouble(), stopOnTarget)