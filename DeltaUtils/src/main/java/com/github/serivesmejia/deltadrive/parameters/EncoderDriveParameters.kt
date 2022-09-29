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

package com.github.serivesmejia.deltadrive.parameters

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacontrol.PIDFCoefficients
import com.github.serivesmejia.deltadrive.utils.DistanceUnit
import com.github.serivesmejia.deltadrive.utils.gear.GearRatio
import com.github.serivesmejia.deltadrive.utils.gear.TwoGearRatio
import com.github.serivesmejia.deltamath.DeltaMathUtil.clamp
import kotlin.math.abs

class EncoderDriveParameters {

    /**
     * Ticks per revolution of the chassis motors.
     * You usually find this value in the page you bought the motors from.
     */
    var TICKS_PER_REV = 0.0

    val EMPTY_GEAR_REDUCTION: GearRatio = TwoGearRatio(1.0, 1.0, 0.0)

    /**
     * This is < 1.0 and > 0 if geared UP
     */
    var DRIVE_GEAR_REDUCTION = EMPTY_GEAR_REDUCTION

    /**
     * The wheels diameter, in inches
     */
    var WHEEL_DIAMETER_INCHES = 0.0

    /**
     * The right side % (in decimal) of speed, from -1 to 1
     */
    var RIGHT_WHEELS_TURBO = 1.0

    /**
     * The left side % (in decimal) of speed, from -1 to 1
     */
    var LEFT_WHEELS_TURBO = 1.0

    /**
     * The right side % (in decimal) of speed while strafing, from -1 to 1
     */
    var RIGHT_WHEELS_STRAFE_TURBO = 1.0

    /**
     * The left side % (in decimal) of speed while strafing, from -1 to 1
     */
    var LEFT_WHEELS_STRAFE_TURBO = 1.0

    /**
     * The middle H-drive wheel % (in decimal) of speed while strafing, from -1 to 1
     */
    var HDRIVE_WHEEL_STRAFE_TURBO = 1.0

    /**
     * The unit which will be used in the movement methods.
     */
    var DISTANCE_UNIT: DistanceUnit = DistanceUnit.INCHES

    /**
     * Show current distance in telemetry (might slow down loop times)
     */
    var SHOW_CURRENT_DISTANCE = false

    /**
     * Requirements to be required by the DeltaCommand
     * of the Task returned by the rotate() method.
     */
    var TASK_COMMAND_REQUIREMENTS = arrayOf<DeltaSubsystem>()

    var DRIVE_STRAIGHT_COEFFICIENTS = PIDFCoefficients(0.0, 0.0, 0.0, 0.0)

    var DRIVE_STRAIGHT_DEADZONE = 0.0

    var DRIVE_STRAIGHT_DEGREE_TOLERANCE = 3.0

    /**
     * Make sure the values are in the correct range (0 to 1 or positive).
     */
    fun secureParameters() {
        RIGHT_WHEELS_TURBO = clamp(RIGHT_WHEELS_TURBO, -1.0, 1.0)
        LEFT_WHEELS_TURBO = clamp(LEFT_WHEELS_TURBO, -1.0, 1.0)

        RIGHT_WHEELS_STRAFE_TURBO = clamp(RIGHT_WHEELS_STRAFE_TURBO, -1.0, 1.0)
        LEFT_WHEELS_STRAFE_TURBO = clamp(LEFT_WHEELS_STRAFE_TURBO, -1.0, 1.0)
        HDRIVE_WHEEL_STRAFE_TURBO = clamp(HDRIVE_WHEEL_STRAFE_TURBO, -1.0, 1.0)

        WHEEL_DIAMETER_INCHES = abs(WHEEL_DIAMETER_INCHES)
        TICKS_PER_REV = abs(TICKS_PER_REV)
    }

}