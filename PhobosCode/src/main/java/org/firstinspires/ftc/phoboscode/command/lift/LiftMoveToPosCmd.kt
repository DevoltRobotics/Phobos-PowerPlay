package org.firstinspires.ftc.phoboscode.command.lift

import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.subsystem
import org.firstinspires.ftc.phoboscode.subsystem.Lift
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem

class LiftMoveToPosCmd(val position: Double) : DeltaCommand() {

    val sub = require<LiftSubsystem>()

    override fun init() {
        sub.leftController.reset()
        sub.leftController.targetPosition = position

        sub.rightController.reset()
        sub.rightController.targetPosition = position
    }

    override fun run() {
        sub.leftMotor.power = sub.leftController.update(sub.leftMotor.currentPosition.toDouble())
        sub.rightMotor.power = sub.rightController.update(sub.rightMotor.currentPosition.toDouble())
    }

}