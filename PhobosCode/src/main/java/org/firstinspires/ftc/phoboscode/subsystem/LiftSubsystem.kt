package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd
import kotlin.math.sign

class LiftSubsystem(
        val leftMotor: DcMotorEx,
        val rightMotor: DcMotorEx,
        val topLimitSensor: RevTouchSensor,
        val bottomLimitSensor: RevTouchSensor
) : DeltaSubsystem() {

    val controller = PIDFController(Lift.pid)

    var power: Double
        get() = leftMotor.power
        set(value) {
            if(bottomLimitSensor.isPressed && value < 0) {
                leftMotor.power = Lift.F
                rightMotor.power = Lift.F
                return
            } else if(topLimitSensor.isPressed && value > 0) {
                leftMotor.power = Lift.F
                rightMotor.power = Lift.F
                return
            }

            leftMotor.power = value + (sign(value) * Lift.F)
            rightMotor.power = value + (sign(value) * Lift.F)
        }

    init {
        leftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        leftMotor.direction = DcMotorSimple.Direction.REVERSE
    }

    override fun loop() {

    }

}

@Config
object Lift {
    @JvmField var pid = PIDCoefficients()

    @JvmField var F = 0.12

    @JvmField var highPos = 2400
    @JvmField var midPos = 1000
    @JvmField var lowPos = 400
}