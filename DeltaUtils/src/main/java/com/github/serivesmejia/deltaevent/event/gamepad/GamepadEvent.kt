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

package com.github.serivesmejia.deltaevent.event.gamepad

import com.github.serivesmejia.deltaevent.gamepad.GamepadDataPacket
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.github.serivesmejia.deltaevent.gamepad.button.Buttons
import com.github.serivesmejia.deltaevent.event.Event
import java.util.*

open class GamepadEvent : Event {

    var left_stick_x = 0.0
    var left_stick_y = 0.0

    var right_stick_x = 0.0
    var right_stick_y = 0.0

    var left_trigger = 0.0
    var right_trigger = 0.0

    val A = Button.A
    val B = Button.B
    val X = Button.X
    val Y = Button.Y

    val DPAD_UP = Button.DPAD_UP
    val DPAD_DOWN = Button.DPAD_DOWN
    val DPAD_LEFT = Button.DPAD_LEFT
    val DPAD_RIGHT = Button.DPAD_RIGHT

    val LEFT_BUMPER = Button.LEFT_BUMPER
    val RIGHT_BUMPER = Button.RIGHT_BUMPER

    val LEFT_TRIGGER = Button.LEFT_TRIGGER
    val RIGHT_TRIGGER = Button.RIGHT_TRIGGER

    val LEFT_STICK_BUTTON = Button.LEFT_STICK_BUTTON
    val RIGHT_STICK_BUTTON = Button.RIGHT_STICK_BUTTON

    val BUTTONS_BEING_PRESSED = Buttons.Type.BUTTONS_BEING_PRESSED
    val BUTTONS_PRESSED = Buttons.Type.BUTTONS_PRESSED
    val BUTTONS_RELEASED = Buttons.Type.BUTTONS_RELEASED

    override fun execute(arg1: Any, arg2: Any) {
        execute(arg1)
    }

    override fun execute(arg1: Any) {
        require(arg1 is GamepadDataPacket) { "Object is not a GamepadDataPacket" }
        left_stick_x = arg1.left_stick_x
        left_stick_y = arg1.left_stick_y
        right_stick_x = arg1.right_stick_x
        right_stick_y = arg1.right_stick_y
        left_trigger = arg1.left_trigger
        right_trigger = arg1.right_trigger
        loop(arg1)
        performEvent(arg1)
    }

    open fun performEvent(gdp: GamepadDataPacket) { }

    override fun execute(args: ArrayList<Any>) {
        for (obj in args) {
            execute(obj)
        }
    }

    override fun execute(args: HashMap<Any, Any>) {
        for ((key, value) in args) {
            execute(key, value)
        }
    }

    /**
     * Method to be executed REPETITIVELY every time the SuperGamepad updates.
     * @param gdp the last GamepadDataPacket
     */
    open fun loop(gdp: GamepadDataPacket) {}

}