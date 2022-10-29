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
            var pow = value

            if(pow < 0) {
                if(bottomLimitSensor.isPressed) {
                    pow = 0.0
                } else {
                    pow *= 0.7
                }
            } else if(topLimitSensor.isPressed && pow > 0) {
                pow = 0.0
            }

            leftMotor.power = pow + Lift.F
            rightMotor.power = pow + Lift.F
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
    @JvmField var pid = PIDCoefficients(0.005, 0.0, 0.0)

    @JvmField var F = 0.08

    @JvmField var highPos = 1200
    @JvmField var midPos = 1000
    @JvmField var lowPos = 400
}