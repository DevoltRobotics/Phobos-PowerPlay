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

package com.github.serivesmejia.deltaevent.gamepad

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltaevent.Super
import com.github.serivesmejia.deltaevent.event.Event
import com.github.serivesmejia.deltaevent.event.gamepad.SuperGamepadEvent
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.github.serivesmejia.deltaevent.gamepad.button.Buttons
import com.github.serivesmejia.deltaevent.gamepad.dsl.SuperGamepadDslBuilder
import com.qualcomm.robotcore.hardware.Gamepad

@Suppress("UNUSED")
class SuperGamepad (var gamepad: Gamepad) : Super {

    override var events = ArrayList<Event>()

    private val pressedButtons = ArrayList<Button>()
    private val ticksPressedButtons = ArrayList<ButtonTicks>()

    init {
        for (btt in Button.values()) {
            val bt = ButtonTicks()
            bt.button = btt
            ticksPressedButtons.add(bt)
        }
    }

    /**
     * Register an event
     * @param event the SuperGamepadEvent to register
     * @return itself
     */
    override fun registerEvent(event: Event): SuperGamepad {
        events.add(event)
        return this
    }

    /**
     * Unregister all the events
     */
    override fun unregisterEvents() {
        events.clear()
    }

    /**
     * Schedule a command to the DeltaScheduler when a button is pressed
     * @param btt The button to be watched for press
     * @params cmd The command to be scheduled when the watched button is pressed
     */
    fun scheduleOnPress(btt: Button, cmd: DeltaCommand, condition: () -> Boolean = { true }) {
        registerEvent(object: SuperGamepadEvent() {
            override fun buttonsPressed(buttons: Buttons) {
                if(buttons(btt) && condition()) deltaScheduler.schedule(cmd)
            }
        })
    }

    /**
     * Schedule a command to the DeltaScheduler when a button is released
     * @param btt The button to be watched for release
     * @params cmd The command to be scheduled when the watched button is released
     */
    fun scheduleOnRelease(btt: Button, cmd: DeltaCommand, condition: () -> Boolean = { true }) {
        registerEvent(object: SuperGamepadEvent() {
            override fun buttonsReleased(buttons: Buttons) {
                if(buttons(btt) && condition()) deltaScheduler.schedule(cmd)
            }
        })
    }

    /**
     * Schedule two commands to the DeltaScheduler when a button is pressed and released, respectively
     * @param btt The button to be watched for press&release
     * @params pressCmd The command to be scheduled when the watched button is pressed
     * @params releaseCmd The command to be scheduled when the watched button is released
     */
    fun scheduleOn(btt: Button, pressCmd: DeltaCommand, releaseCmd: DeltaCommand) {
        scheduleOnPress(btt, pressCmd)
        scheduleOnRelease(btt, releaseCmd)
    }

    /**
     * Schedule a command to the DeltaScheduler when a button is pressed and released
     * @param btt The button to be watched for press&release
     * @params pressAndReleaseCmd The command to be scheduled when the watched button is pressed and released
     */
    fun scheduleOn(btt: Button, pressAndReleaseCmd: DeltaCommand) {
        scheduleOnPress(btt, pressAndReleaseCmd)
        scheduleOnRelease(btt, pressAndReleaseCmd)
    }

    fun toggleScheduleOn(btt: Button, toggleOnCmd: DeltaCommand, toggleOffCmd: DeltaCommand) {
        registerEvent(object: SuperGamepadEvent() {
            var toggleState = false

            override fun buttonsPressed(buttons: Buttons) {
                if(buttons(btt)) {
                    toggleState = !toggleState

                    if(toggleState) {
                        + toggleOnCmd
                        toggleOffCmd.requestEnd()
                    } else {
                        + toggleOffCmd
                        toggleOnCmd.requestEnd()
                    }
                }
            }
        })
    }

    /**
     * By attaching the SuperGamepad to the DeltaScheduler, it
     * will be updated when the DeltaScheduler is updated.
     * This can be useful in a case when you want to use only
     * the commander paradigm with the scheduleOn methods, so that
     * you don't need to update the gamepads on your loop, only
     * the DeltaScheduler
     */
    fun attachToScheduler() {
        deltaScheduler.onRunScheduler { update() }
    }

    /**
     * Update the pressed buttons and execute all the events.
     * This method should be placed at the end or at the start of your repeat in your OpMode
     */
    override fun update() {
        val gdp = GamepadDataPacket()

        pressedButtons.clear()
        updatePressedButtons()

        gdp.left_stick_x = gamepad.left_stick_x.toDouble()
        gdp.left_stick_y = gamepad.left_stick_y.toDouble()
        gdp.right_stick_x = gamepad.right_stick_x.toDouble()
        gdp.right_stick_y = gamepad.right_stick_y.toDouble()
        gdp.left_trigger = gamepad.left_trigger.toDouble()
        gdp.right_trigger = gamepad.right_trigger.toDouble()
        gdp.gamepad = gamepad

        for (btt in pressedButtons) {
            val bt = getElementFromTicksPressedButtons(btt)

            if(bt != null) {
                bt.ticks++
                gdp.buttonsBeingPressed[btt] = bt.ticks

                if (bt.ticks == 1) {
                    gdp.buttonsPressed[btt] = bt.ticks
                }
            }
        }

        for (bt in ticksPressedButtons) {
            if (bt.ticks <= 0) continue

            if (!buttonIsPressed(bt.button)) {
                bt.ticks = 0
                gdp.buttonsReleased[bt.button] = bt.ticks
            }
        }

        updateAllEvents(gdp)
    }

    operator fun invoke(block: SuperGamepadDslBuilder.() -> Unit) {
        SuperGamepadDslBuilder(this, block).build()
    }

    private fun updateAllEvents(gdp: GamepadDataPacket) {
        for (evt in events) {
            require(evt is SuperGamepadEvent) { "Event is not a SuperGamepadEvent" }
            evt.execute(gdp)
        }
    }

    //mapping all buttons
    private fun updatePressedButtons() {
        if (gamepad.a) pressedButtons.add(Button.A)
        if (gamepad.b) pressedButtons.add(Button.B)
        if (gamepad.x) pressedButtons.add(Button.X)
        if (gamepad.y) pressedButtons.add(Button.Y)
        if (gamepad.dpad_up) pressedButtons.add(Button.DPAD_UP)
        if (gamepad.dpad_down) pressedButtons.add(Button.DPAD_DOWN)
        if (gamepad.dpad_left) pressedButtons.add(Button.DPAD_LEFT)
        if (gamepad.dpad_right) pressedButtons.add(Button.DPAD_RIGHT)
        if (gamepad.right_bumper) pressedButtons.add(Button.RIGHT_BUMPER)
        if (gamepad.left_bumper) pressedButtons.add(Button.LEFT_BUMPER)
        if (gamepad.left_stick_button) pressedButtons.add(Button.LEFT_STICK_BUTTON)
        if (gamepad.right_stick_button) pressedButtons.add(Button.RIGHT_STICK_BUTTON)
        if (gamepad.right_trigger > 0.5) pressedButtons.add(Button.RIGHT_TRIGGER)
        if (gamepad.left_trigger > 0.5) pressedButtons.add(Button.LEFT_TRIGGER)

        if(gamepad.left_stick_y > 0.5) pressedButtons.add(Button.LEFT_STICK_Y)
        if(gamepad.left_stick_y < -0.5) pressedButtons.add(Button.LEFT_STICK_Y)
        if(gamepad.left_stick_x > 0.5) pressedButtons.add(Button.LEFT_STICK_X)
        if(gamepad.left_stick_x < -0.5) pressedButtons.add(Button.LEFT_STICK_X)

        if(gamepad.right_stick_y > 0.5) pressedButtons.add(Button.RIGHT_STICK_Y)
        if(gamepad.right_stick_y < -0.5) pressedButtons.add(Button.RIGHT_STICK_Y)
        if(gamepad.right_stick_x > 0.5) pressedButtons.add(Button.RIGHT_STICK_X)
        if(gamepad.right_stick_x < -0.5) pressedButtons.add(Button.RIGHT_STICK_X)
    }

    private fun buttonIsPressed(btt: Button): Boolean {
        return pressedButtons.contains(btt)
    }

    private fun getElementFromTicksPressedButtons(element: Button): ButtonTicks? {
        for (bt in ticksPressedButtons) {
            if (bt.button == element) return bt
        }
        return null
    }

    private class ButtonTicks {
        var ticks = 0
        var button = Button.NONE
    }
}