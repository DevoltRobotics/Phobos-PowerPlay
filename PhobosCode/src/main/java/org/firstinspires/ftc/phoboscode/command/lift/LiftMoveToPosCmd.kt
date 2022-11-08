package org.firstinspires.ftc.phoboscode.command.lift

import com.github.serivesmejia.deltacommander.DeltaCommand
import org.firstinspires.ftc.phoboscode.subsystem.Lift
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem

open class LiftMoveToPosCmd(val position: Double, val stopOnTarget: Boolean = false) : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    var controller = sub.liftController

    override fun init() {
        if(-sub.leftMotor.currentPosition + position < 0) controller = sub.downwardsController

        controller.reset()
        controller.targetPosition = position
    }

    override fun run() {
        sub.power = controller.update(-sub.leftMotor.currentPosition.toDouble())
    }

    override fun end(interrupted: Boolean) {
        sub.power = 0.0
    }
}

class LiftMoveToHighCmd(stopOnTarget: Boolean = true) : LiftMoveToPosCmd(Lift.highPos.toDouble(), stopOnTarget)
class LiftMoveToMidCmd(stopOnTarget: Boolean = true) : LiftMoveToPosCmd(Lift.midPos.toDouble(), stopOnTarget)
class LiftMoveToLowCmd(stopOnTarget: Boolean = true) : LiftMoveToPosCmd(Lift.lowPos.toDouble(), stopOnTarget)