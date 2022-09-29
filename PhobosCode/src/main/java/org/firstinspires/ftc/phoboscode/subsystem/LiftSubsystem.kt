package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd

class LiftSubsystem(val leftMotor: DcMotorEx, val rightMotor: DcMotorEx) : DeltaSubsystem() {

    val leftController = PIDFController(Lift.leftPID)
    val rightController = PIDFController(Lift.rightPID)

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