package org.firstinspires.ftc.phoboscode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.github.serivesmejia.deltaevent.opmode.DeltaOpMode
import org.firstinspires.ftc.phoboscode.subsystem.LiftSubsystem
import org.firstinspires.ftc.phoboscode.subsystem.MecanumSubsystem
import org.firstinspires.ftc.phoboscode.subsystem.TurretSubsystem

abstract class PhobosOpMode : DeltaOpMode() {

    override val hardware = PhobosHardware()

    lateinit var mecanumSub: MecanumSubsystem
    lateinit var turretSubsystem: TurretSubsystem
    lateinit var liftSubsystem: LiftSubsystem

    override fun initialize() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        mecanumSub = MecanumSubsystem(hardware.drive)
        turretSubsystem = TurretSubsystem(hardware.turretMotor)
        liftSubsystem = LiftSubsystem(hardware.sliderLeftMotor, hardware.sliderRightMotor)

        setup()
    }

    abstract fun setup()

}