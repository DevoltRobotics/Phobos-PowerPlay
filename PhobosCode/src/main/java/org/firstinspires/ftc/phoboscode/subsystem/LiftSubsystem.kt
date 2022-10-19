package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd

class LiftSubsystem(val leftMotor: DcMotorEx, val rightMotor: DcMotorEx, val touchSensor: RevTouchSensor) : DeltaSubsystem() {

    val leftController = PIDFController(Lift.leftPID)
    val rightController = PIDFController(Lift.rightPID)

    var power = 0.0
        set(value) {
            leftMotor.power = power
            rightMotor.power = power
            field = value
        }

    init {
        leftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        leftMotor.direction = DcMotorSimple.Direction.REVERSE

        defaultCommand = LiftMoveCmd(0.1)
    }

    override fun loop() {

    }

}

@Config
object Lift {
    @JvmField var leftPID = PIDCoefficients()
    @JvmField var rightPID = PIDCoefficients()
}