package com.github.serivesmejia.deltadrive

import com.github.serivesmejia.deltadrive.utils.task.Task
import com.qualcomm.robotcore.hardware.Gamepad

interface DeltaHolonomicDrivebase : DeltaDrivebase {

    override fun joystickRobotCentric(forwardSpeed: Double, turnSpeed: Double, turbo: Double) =
        joystickRobotCentric(forwardSpeed, 0.0, turnSpeed, turbo)

    fun joystickRobotCentric(forwardSpeed: Double, strafeSpeed: Double, turnSpeed: Double, turbo: Double)

    fun joystickFieldCentric(forwardSpeed: Double, strafeSpeed: Double, turnSpeed: Double, turbo: Double)

    fun joystickFieldCentric(gamepad: Gamepad, turbo: Double = 1.0)

    fun joystickFieldCentric(gamepad: Gamepad, controlSpeedWithTriggers: Boolean, maxMinusTurbo: Double = 0.8)
    
    fun timeStrafeLeft(power: Double, timeSecs: Double): Task<Unit>
    fun timeStrafeRight(power: Double, timeSecs: Double): Task<Unit>

    fun encoderStrafeLeft(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>
    fun encoderStrafeRight(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>

    fun encoderTiltForwardLeft(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>
    fun encoderTiltForwardRight(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>

    fun encoderTiltBackwardsLeft(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>
    fun encoderTiltBackwardsRight(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>

}