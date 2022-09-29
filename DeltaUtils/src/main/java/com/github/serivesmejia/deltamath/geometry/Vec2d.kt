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
import kotlin.math.hypot

class Vec2d(val x: Double = 0.0, val y: Double = 0.0) {

    /**
     * Constructor for Vec2d using another Vec2d
     * @param o
     */
    constructor (o: Vec2d) : this(o.x, o.y)

    val radians = atan2(y, x)

    /**
     * the magnitude of the vector
     */
    val mag = hypot(x, y)

    operator fun plus(o: Vec2d): Vec2d {
        val newX = x + o.x
        val newY = y + o.y

        return Vec2d(newX, newY)
    }

    operator fun minus(o: Vec2d): Vec2d {
        val newX = x - o.x
        val newY = y - o.y

        return Vec2d(newX, newY)
    }

    operator fun div(o: Vec2d): Vec2d {
        val newX = x / o.x
        val newY = y / o.y

        return Vec2d(newX, newY)
    }


    operator fun times(o: Vec2d): Vec2d {
        val newX = x * o.x
        val newY = y * o.y

        return Vec2d(newX, newY)
    }


    /**
     * Rotate this Vec2d by a Rot2d
     * @param by the Rot2d to rotate by
     */
    fun rotate(by: Rot2d) = Vec2d(
            x * by.cos - y * by.sin,
            x * by.sin + y * by.cos
    )

    /**
     * Inverts current Vec2d values to negative/positive
     */
    fun invert() = Vec2d(-x, -y)

    override fun toString(): String {
        return "Vec2d($x, $y)"
    }

}