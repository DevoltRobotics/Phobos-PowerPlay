package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd
import kotlin.math.abs

class TurretSubsystem(val motor: DcMotorEx) : DeltaSubsystem() {

    val controller = PIDFController(Turret.pid)
    val trackingController = PIDFController(Turret.trackingPid)

    val angle get() = motor.currentPosition / Turret.ticksPerAngle

    val isOnTarget get() = abs(controller.lastError) > 5 * Turret.ticksPerAngle

    init {
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    override fun loop() {
    }

    fun reset() {
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

}


@Config
object Turret {
    @JvmField var pid = PIDCoefficients(0.005, 0.0, 0.0)
    @JvmField var trackingPid = PIDCoefficients(0.003, 0.0, 0.0)

    val ticksPerRev = 1120
    val gearRatio = 119.0 / 32

    @JvmField var maxDegreesPerSecond = 90.0

    val ticksPerAngle = (ticksPerRev * gearRatio) / 360.0
}