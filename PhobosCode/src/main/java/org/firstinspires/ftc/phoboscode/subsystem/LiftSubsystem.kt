package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.hardware.*

class LiftSubsystem(
        val leftMotor: DcMotorEx,
        val rightMotor: DcMotorEx,
        val topLimitSensor: RevColorSensorV3,
        val bottomLimitSensor: RevTouchSensor
) : DeltaSubsystem() {

    val liftController = PIDFController(Lift.liftPid)
    val downwardsController = PIDFController(Lift.downwardsPid)

    var liftTurbo = 0.9

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
                } else {
                    pow *= 0.2
                }
            } else if(lastTopRed >= 800 && pow > 0) {
                pow = 0.0
            } else {
                pow *= liftTurbo
                pow += Lift.F
            }

            leftMotor.power = pow
            rightMotor.power = pow
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
    @JvmField var moveDownPower = -0.0008
    @JvmField var liftPid = PIDCoefficients(0.0021, 0.0005, 0.0003)
    @JvmField var downwardsPid = PIDCoefficients(0.000000001, 0.0, 0.0)

    @JvmField var F = 0.08
    @JvmField var highPos = 1590
    @JvmField var midPos = 1100
    @JvmField var lowPos = 780
}