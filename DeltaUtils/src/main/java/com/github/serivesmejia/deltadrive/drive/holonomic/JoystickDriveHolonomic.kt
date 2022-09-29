/*
 * Copyright (c) 2020 FTC Delta Robotics #9351 - Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.serivesmejia.deltadrive.drive.holonomic

import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltamath.geometry.Vec2d
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.Range
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Class to control an holonomic chan ssis during teleop using a gamepad's joysticks.
 */
class JoystickDriveHolonomic
/**
 * Constructor for the Joystick Drive
 * @param hdw The initialized hardware containing all the chassis motors
 */
(private val hdw: DeltaHardwareHolonomic, var gamepad: Gamepad? = null) {

    //wheel motor power
    private var wheelFrontRightPower = 0.0
    private var wheelFrontLeftPower = 0.0
    private var wheelBackRightPower = 0.0
    private var wheelBackLeftPower = 0.0

    /**
     * Control a mecanum chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called always in the teleop repeat to update the motor powers
     * @param turbo the chassis % of speed, from 0 to 1
     */
    fun update(turbo: Double) = update(turbo, turbo)

    /**
     * Control a mecanum chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called constantly in the teleop loop to update the motor powers
     * @param rightTurbo the chassis right side % of speed, from 0 to 1
     * @param leftTurbo the chassis left side % of speed, from 0 to 1
     */
    fun update(rightTurbo: Double, leftTurbo: Double) {
        val y1 = -gamepad!!.left_stick_y.toDouble()
        val x1 = gamepad!!.left_stick_x.toDouble()
        val x2 = gamepad!!.right_stick_x.toDouble()

        update(y1, x1, x2, rightTurbo, leftTurbo)
    }

    /**
     * Control a mecanum chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called constantly in the teleop loop to update the motor powers
     * @param y1 forward-backwards speed
     * @param x1 diagonal speed (strafing)
     * @poram x2 rotation speed
     * @param rightTurbo the chassis right side % of speed, from 0 to 1
     * @param leftTurbo the chassis left side % of speed, from 0 to 1
     */
    fun update(y1: Double, x1: Double, x2: Double, rightTurbo: Double, leftTurbo: Double) {
        val rt = Range.clip(abs(rightTurbo), 0.0, 1.0)
        val lt = Range.clip(abs(leftTurbo), 0.0, 1.0)

        wheelFrontRightPower = y1 - x2 - x1
        wheelBackRightPower = y1 - x2 + x1
        wheelFrontLeftPower = y1 + x2 + x1
        wheelBackLeftPower = y1 + x2 - x1

        val max = abs(wheelFrontRightPower)
                    .coerceAtLeast(abs(wheelBackRightPower)
                        .coerceAtLeast(abs(wheelFrontLeftPower)
                            .coerceAtLeast(abs(wheelBackLeftPower))))

        if (max > 1.0) {
            wheelFrontRightPower /= max
            wheelBackRightPower /= max
            wheelFrontLeftPower /= max
            wheelBackLeftPower /= max
        }

        wheelFrontRightPower *= rt
        wheelBackRightPower *= rt
        wheelFrontLeftPower *= lt
        wheelBackLeftPower *= lt

        hdw.setMotorPowers(wheelFrontLeftPower, wheelFrontRightPower, wheelBackLeftPower, wheelBackRightPower)
    }

    fun update(
            y1: Double, x1: Double, x2: Double,
            rightTurbo: Double, leftTurbo: Double, angle: Rot2d
    ) {
        val input = Vec2d(clip(x1), clip(y1))
        val rotated = input.rotate(angle.invert())
        val theta = rotated.radians

        var fl = sin(theta + PI / 4)
        var fr = sin(theta - PI / 4)
        var bl = sin(theta - PI / 4)
        var br = sin(theta + PI / 4)

        val maxMag = abs(fl).coerceAtLeast(abs(fr).coerceAtLeast(abs(bl).coerceAtLeast(abs(br))))
        val mag = input.mag

        fl = ((fl / mag) * maxMag) + x2
        fr = ((fr / mag) * maxMag) - x2
        bl = ((bl / mag) * maxMag) + x2
        br = ((br / mag) * maxMag) - x2

        val max = abs(fl).coerceAtLeast(abs(fr).coerceAtLeast(abs(bl).coerceAtLeast(abs(br))))

        if (max > 1.0) {
            wheelFrontRightPower /= max
            wheelBackRightPower /= max
            wheelFrontLeftPower /= max
            wheelBackLeftPower /= max
        }

        val rt = abs(rightTurbo)
        val lt = abs(leftTurbo)

        wheelFrontRightPower *= rt
        wheelBackRightPower *= rt
        wheelFrontLeftPower *= lt
        wheelBackLeftPower *= lt

        hdw.setMotorPowers(fl, fr, bl, br)
    }

    /**
     * Control a mecanum chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called constantly in the teleop loop to update the motor powers
     * @param rightTurbo the chassis right side % of speed, from 0 to 1
     * @param leftTurbo the chassis left side % of speed, from 0 to 1
     */
    fun update(rightTurbo: Double, leftTurbo: Double, angle: Rot2d) {
        val y1 = -gamepad!!.left_stick_y.toDouble()
        val x1 = gamepad!!.left_stick_x.toDouble()
        val x2 = gamepad!!.right_stick_x.toDouble()

        update(y1, x1, x2, rightTurbo, leftTurbo, angle)
    }

    /**
     * Control a mecanum chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called constantly in the teleop loop to update the motor powers
     * @param rightTurbo the chassis right side % of speed, from 0 to 1
     * @param leftTurbo the chassis left side % of speed, from 0 to 1
     */
    fun update(turbo: Double, angle: Rot2d) = update(turbo, turbo, angle)

    private fun clip(number: Double) = Range.clip(abs(number), 0.0, 1.0)

}