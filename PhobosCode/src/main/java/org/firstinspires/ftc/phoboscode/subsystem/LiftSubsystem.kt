package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd
import kotlin.math.sign

class LiftSubsystem(
        val leftMotor: DcMotorEx,
        val rightMotor: DcMotorEx,
        val topLimitSensor: RevColorSensorV3,
        val bottomLimitSensor: RevTouchSensor
) : DeltaSubsystem() {

    val controller = PIDFController(Lift.pid)

    var lastTopRed = 0
        private set

    var power: Double
        get() = leftMotor.power
        set(value) {
            var pow = value

            lastTopRed = topLimitSensor.red()

            if(pow < 0) {
                if(bottomLimitSensor.isPressed) {
                    pow = 0.0
                    reset()
                } else {
                    pow *= 0.7
                }
            } else if(lastTopRed >= 800 && pow > 0) {
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

    fun reset() {
        leftMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        leftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

}

@Config
object Lift {
    @JvmField var pid = PIDCoefficients(0.005, 0.0, 0.0)

    @JvmField var F = 0.08

    @JvmField var highPos = 1900
    @JvmField var midPos = 1700
    @JvmField var lowPos = 1200
}