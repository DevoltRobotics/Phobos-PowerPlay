package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd

class LiftSubsystem(
        val leftMotor: DcMotorEx,
        val rightMotor: DcMotorEx,
        val bottomLimitSensor: RevTouchSensor
) : DeltaSubsystem() {

    val controller = PIDFController(Lift.pid)

    var power: Double
        get() = leftMotor.power
        set(value) {
            if(bottomLimitSensor.isPressed && value < 0) {
                leftMotor.power = 0.0
                rightMotor.power = 0.0
                return
            }

            leftMotor.power = value
            rightMotor.power = value
        }

    init {
        leftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        leftMotor.direction = DcMotorSimple.Direction.REVERSE

        //defaultCommand = LiftMoveCmd(0.2)
    }

    override fun loop() {
    }

}

@Config
object Lift {
    @JvmField var pid = PIDCoefficients()

    @JvmField var highPos = 2400
    @JvmField var midPos = 1000
    @JvmField var lowPos = 400
}