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

package com.github.serivesmejia.deltamath

/**
 * Class with some WPILib FRC Lib math methods and extra Delta´s ones.
 */
object DeltaMathUtil {

    /**
     * Returns value clamped between low and high boundaries.
     *
     * @param value Value to clamp.
     * @param low   The lower boundary to which to clamp value.
     * @param high  The higher boundary to which to clamp value.
     */
    fun clamp(value: Int, low: Int, high: Int): Int {
        return Math.max(low, Math.min(value, high))
    }

    /**
     * Returns value clamped between low and high boundaries.
     *
     * @param value Value to clamp.
     * @param low   The lower boundary to which to clamp value.
     * @param high  The higher boundary to which to clamp value.
     */
    fun clamp(value: Double, low: Double, high: Double): Double {
        return Math.max(low, Math.min(value, high))
    }

    /**
     * Get the difference (delta) between two angles (in degrees)
     * @param angle1 The angle to be subtracted by the angle2
     * @param angle2 The angle to be subtracted to angle1
     * @return The result of the angles difference, considering the -360 to 360 range.
     */
    fun deltaDegrees(angle1: Double, angle2: Double): Double {
        var deltaAngle = angle1 - angle2
        if (deltaAngle < -180) deltaAngle += 360.0 else if (deltaAngle > 180) deltaAngle -= 360.0
        return deltaAngle
    }

}