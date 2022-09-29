package org.firstinspires.ftc.phoboscode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx

class TurretSubsystem(val motor: DcMotorEx) : DeltaSubsystem() {

    val controller = PIDFController(Turret.pid)

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
    @JvmField var pid = PIDCoefficients()

    val ticksPerRev = 1120
    val gearRatio = 24 / 119.0
    val ticksPerAngle = (ticksPerRev * gearRatio) / 360.0
}