package org.firstinspires.ftc.phoboscode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.github.serivesmejia.deltaevent.opmode.DeltaOpMode
import org.firstinspires.ftc.phoboscode.subsystem.*

abstract class PhobosOpMode : DeltaOpMode() {

    override val hardware = PhobosHardware()

    lateinit var mecanumSub: MecanumSubsystem
    lateinit var intakeArmSubsystem: IntakeArmSubsystem
    lateinit var intakeWheelsSubsystem: IntakeWheelsSubsystem
    lateinit var turretSubsystem: TurretSubsystem
    lateinit var liftSubsystem: LiftSubsystem

    override fun initialize() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        mecanumSub = MecanumSubsystem(hardware.drive)
        turretSubsystem = TurretSubsystem(hardware.turretMotor)
        liftSubsystem = LiftSubsystem(hardware.sliderLeftMotor, hardware.sliderRightMotor, hardware.sliderTopLimitSensor, hardware.sliderBottomLimitSensor)
        intakeArmSubsystem = IntakeArmSubsystem(hardware.intakeArmServo, hardware.intakeTiltServo)
        intakeWheelsSubsystem = IntakeWheelsSubsystem(hardware.intakeLeftServo, hardware.intakeRightServo)

        setup()
    }

    override fun runUpdate() {
        hardware.drive.update()
    }

    abstract fun setup()

}