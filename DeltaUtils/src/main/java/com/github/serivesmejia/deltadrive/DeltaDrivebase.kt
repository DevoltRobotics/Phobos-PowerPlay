package com.github.serivesmejia.deltadrive

import com.github.serivesmejia.deltadrive.utils.task.Task
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltamath.geometry.Twist2d
import com.qualcomm.robotcore.hardware.Gamepad

interface DeltaDrivebase {

    fun joystickRobotCentric(forwardSpeed: Double, turnSpeed: Double, turbo: Double = 0.8)
    fun joystickRobotCentric(gamepad: Gamepad, controlSpeedWithTriggers: Boolean, maxMinusTurbo: Double = 0.8)
    fun joystickRobotCentric(gamepad: Gamepad, turbo: Double = 1.0)

    fun rotate(angle: Rot2d, power: Double, timeoutSecs: Double = 5.0): Task<Twist2d>

    fun timeForward(power: Double, timeSecs: Double): Task<Unit>
    fun timeBackwards(power: Double, timeSecs: Double): Task<Unit>
    fun timeTurnLeft(power: Double, timeSecs: Double): Task<Unit>
    fun timeTurnRight(power: Double, timeSecs: Double): Task<Unit>

    fun encoderForward(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>
    fun encoderBackwards(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>
    fun encoderTurnLeft(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>
    fun encoderTurnRight(distance: Double, power: Double, timeoutSecs: Double = 5.0): Task<Unit>

}