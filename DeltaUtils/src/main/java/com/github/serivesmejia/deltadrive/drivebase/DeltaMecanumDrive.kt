package com.github.serivesmejia.deltadrive.drivebase

import com.github.serivesmejia.deltadrive.DeltaHolonomicDrivebase
import com.github.serivesmejia.deltadrive.drive.holonomic.EncoderDriveHolonomic
import com.github.serivesmejia.deltadrive.drive.holonomic.IMUDrivePIDFHolonomic
import com.github.serivesmejia.deltadrive.drive.holonomic.JoystickDriveHolonomic
import com.github.serivesmejia.deltadrive.drive.holonomic.TimeDriveHolonomic
import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltadrive.utils.task.Task
import com.github.serivesmejia.deltamath.DeltaMathUtil
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

@Suppress("UNUSED")
class DeltaMecanumDrive(
    private val hdw: DeltaHardwareHolonomic,
    private val telemetry: Telemetry? = null
) : DeltaHolonomicDrivebase {

    private val joystickDrive = JoystickDriveHolonomic(hdw)
    private val imuDrive = IMUDrivePIDFHolonomic(hdw, telemetry)
    private lateinit var encoderDrive: EncoderDriveHolonomic
    private val timeDrive = TimeDriveHolonomic(hdw, telemetry)

    val imu get() = imuDrive.imu

    override fun joystickRobotCentric(
        forwardSpeed: Double,
        strafeSpeed: Double,
        turnSpeed: Double,
        turbo: Double
    ) = joystickDrive.update(forwardSpeed, strafeSpeed, turnSpeed, turbo, turbo)

    override fun joystickRobotCentric(
        gamepad: Gamepad,
        controlSpeedWithTriggers: Boolean,
        maxMinusTurbo: Double
    ) {
        joystickDrive.gamepad = gamepad

        val maxMinTurbo = DeltaMathUtil.clamp(abs(maxMinusTurbo), 0.0, 1.0)

        if(controlSpeedWithTriggers) {
            val minusTurbo = when {
                gamepad.left_trigger > 0.2 -> gamepad.left_trigger * maxMinTurbo
                else -> gamepad.right_trigger * maxMinTurbo
            }

            joystickDrive.update(
                DeltaMathUtil.clamp(1 - minusTurbo, 0.0, 1.0)
            )
        } else {
            joystickDrive.update(1.0)
        }
    }

    override fun joystickRobotCentric(gamepad: Gamepad, turbo: Double) {
        joystickDrive.gamepad = gamepad
        joystickDrive.update(turbo)
    }

    override fun joystickFieldCentric(forwardSpeed: Double, strafeSpeed: Double, turnSpeed: Double, turbo: Double) =
        joystickDrive.update(forwardSpeed, strafeSpeed, turnSpeed, turbo, turbo, Rot2d.zero)

    override fun joystickFieldCentric(gamepad: Gamepad, turbo: Double) {
        joystickDrive.gamepad = gamepad
        joystickDrive.update(turbo, turbo, Rot2d.zero)
    }

    override fun joystickFieldCentric(gamepad: Gamepad, controlSpeedWithTriggers: Boolean, maxMinusTurbo: Double) {
        joystickDrive.gamepad = gamepad

        val maxMinTurbo = DeltaMathUtil.clamp(abs(maxMinusTurbo), 0.0, 1.0)

        if(controlSpeedWithTriggers) {
            val minusTurbo = when {
                gamepad.left_trigger > 0.2 -> gamepad.left_trigger * maxMinTurbo
                else -> gamepad.right_trigger * maxMinTurbo
            }

            joystickDrive.update(
                    DeltaMathUtil.clamp(1 - minusTurbo, 0.0, 1.0),
                    Rot2d.zero
            )
        } else {
            joystickDrive.update(1.0, Rot2d.zero)
        }
    }

    fun initIMU(params: IMUDriveParameters) {
        imuDrive.initIMU(params)

        if(::encoderDrive.isInitialized)
            encoderDrive.imu = imuDrive.imu
    }

    fun waitForIMUCalibration() = imuDrive.waitForIMUCalibration()

    fun resetIMU() = imu.resetAngle()

    override fun rotate(angle: Rot2d, power: Double, timeoutSecs: Double) =
        imuDrive.rotate(angle, power, timeoutSecs)

    override fun timeForward(power: Double, timeSecs: Double) =
        timeDrive.forward(power, timeSecs)
    override fun timeBackwards(power: Double, timeSecs: Double) =
        timeDrive.turnRight(power, timeSecs)

    override fun timeTurnLeft(power: Double, timeSecs: Double) =
        timeDrive.turnLeft(power, timeSecs)
    override fun timeTurnRight(power: Double, timeSecs: Double) =
        timeDrive.turnRight(power, timeSecs)

    override fun timeStrafeLeft(power: Double, timeSecs: Double) =
        timeDrive.strafeLeft(power, timeSecs)
    override fun timeStrafeRight(power: Double, timeSecs: Double) =
        timeDrive.strafeRight(power, timeSecs)

    fun initEncoders(parameters: EncoderDriveParameters) {
        encoderDrive = EncoderDriveHolonomic(
                hdw, parameters, telemetry, if(imuDrive.isIMUCalibrated) imu else null
        )
    }

    override fun encoderForward(distance: Double, power: Double, timeoutSecs: Double) =
        encoderDrive.forward(distance, power, timeoutSecs)
    override fun encoderBackwards(distance: Double, power: Double, timeoutSecs: Double) =
        encoderDrive.backwards(distance, power, timeoutSecs)

    override fun encoderTurnLeft(distance: Double, power: Double, timeoutSecs: Double) =
        encoderDrive.turnLeft(distance, power, timeoutSecs)
    override fun encoderTurnRight(distance: Double, power: Double, timeoutSecs: Double) =
        encoderDrive.turnRight(distance, power, timeoutSecs)

    override fun encoderStrafeLeft(distance: Double, power: Double, timeoutSecs: Double) =
        encoderDrive.strafeLeft(distance, power, timeoutSecs)
    override fun encoderStrafeRight(distance: Double, power: Double, timeoutSecs: Double) =
        encoderDrive.strafeRight(distance, power, timeoutSecs)

    override fun encoderTiltForwardLeft(distance: Double, power: Double, timeoutSecs: Double) =
        encoderDrive.tiltForwardLeft(distance, power, timeoutSecs)

    override fun encoderTiltForwardRight(distance: Double, power: Double, timeoutSecs: Double) =
            encoderDrive.tiltForwardRight(distance, power, timeoutSecs)

    override fun encoderTiltBackwardsLeft(distance: Double, power: Double, timeoutSecs: Double) =
            encoderDrive.tiltBackwardsLeft(distance, power, timeoutSecs)

    override fun encoderTiltBackwardsRight(distance: Double, power: Double, timeoutSecs: Double) =
            encoderDrive.tiltBackwardsRight(distance, power, timeoutSecs)

}