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

package com.github.serivesmejia.deltamath.geometry

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class Rot2d(val radians: Double = 0.0) {

    companion object {
        val zero = Rot2d()

        /**
         * Creates a new Rot2d from degrees
         * @param degrees degrees to set to the new Rot2d
         * @return new Rot2d from degrees
         */
        fun degrees(degrees: Double) = Rot2d(Math.toRadians(degrees))
        
        fun vector(x: Double, y: Double): Rot2d {
            val hy = hypot(x, y)

            val (sin, cos)=  if (hy > 0.00001) {
                Pair(y / hy, x / hy)
            } else {
                Pair(0.0, 1.0)
            }

            return Rot2d(atan2(sin, cos))
        }
        
        fun vector(vec: Vec2d) = vector(vec.x, vec.y)
    }

    val cos = cos(radians)
    val sin = sin(radians)
    
    val degrees = Math.toDegrees(radians)

    /**
     * @param other Other Rot2d
     * @return the difference in radians between this and other Rot2d
     */
    fun deltaRadians(other: Rot2d): Double { return Math.toRadians(deltaDegrees(other)) }

    /**
     * @param other Other Rot2d
     * @return the difference in degrees between this and other Rot2d
     */
    fun deltaDegrees(other: Rot2d): Double {
        var deltaAngle = degrees - other.degrees
        if (deltaAngle < -180) deltaAngle += 360.0 else if (deltaAngle > 180) deltaAngle -= 360.0

        return deltaAngle
    }

    /**
     * the calculated tangent
     */
    val tan = sin / cos

    /**
     * Rotate by another Rot2d and returns a new one
     * @param o the Rot2d to rotate by
     * @return Result Rot2d
     */
    fun rotate(o: Rot2d) = Rot2d.vector(
        cos * o.cos - o.sin * o.cos,
        cos * o.sin + o.sin * o.cos
    )

    operator fun plus(o: Rot2d) = rotate(o)

    operator fun minus(o: Rot2d) = rotate(o.invert())
    
    operator fun div(o: Rot2d) = Rot2d(radians / o.radians)

    operator fun times(o: Rot2d) = Rot2d(radians * o.radians)


    /**
     * Inverts the radians and returns a new Rot2d
     * @return Result Rot2d
     */
    fun invert() = Rot2d(-radians)
    
    override fun toString() = "Rot2d(rad ${radians}, deg ${Math.toDegrees(radians)})"

}