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
import com.github.serivesmejia.deltaevent.gamepad.button.Buttons

open class SuperGamepadEvent : GamepadEvent() {

    override fun performEvent(gdp: GamepadDataPacket) {
        if (gdp.buttonsBeingPressed.isNotEmpty()) {
            buttonsBeingPressed(Buttons(gdp.buttonsBeingPressed, BUTTONS_BEING_PRESSED))
        }
        if (gdp.buttonsPressed.isNotEmpty()) {
            buttonsPressed(Buttons(gdp.buttonsPressed, BUTTONS_PRESSED))
        }
        if (gdp.buttonsReleased.isNotEmpty()) {
            buttonsReleased(Buttons(gdp.buttonsReleased, BUTTONS_RELEASED))
        }
    }

    /**
     * Method to be executed ONCE when at least one button is pressed
     * @param buttons the pressed buttons
     */
    open fun buttonsPressed(buttons: Buttons) {}

    /**
     * Method to be executed ONCE when at least one button is released
     * @param buttons the released buttons
     */
    open fun buttonsReleased(buttons: Buttons) {}


    /**
     * Method to be executed REPETITIVELY when at least one button is pressed until it is released
     * @param buttons the being pressed buttons
     */
    open fun buttonsBeingPressed(buttons: Buttons) {}

}