package com.github.serivesmejia.deltaevent.gamepad.dsl

import com.github.serivesmejia.deltaevent.event.gamepad.SuperGamepadEvent
import com.github.serivesmejia.deltaevent.gamepad.SuperGamepad
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.github.serivesmejia.deltaevent.gamepad.button.Buttons

class SuperGamepadDslBuilder(
    private val superGamepad: SuperGamepad,
    private val block: SuperGamepadDslBuilder.() -> Unit
) {

    private val buttons = mutableMapOf<Button, SuperGamepadDslButtonBuilder>()

    operator fun Button.invoke(block: SuperGamepadDslButtonBuilder.() -> Unit) {
        buttons[this] = SuperGamepadDslButtonBuilder(block)
    }

    fun Button.pressed(callback: () -> Unit) {
        buttons[this] = SuperGamepadDslButtonBuilder {
            pressed(callback)
        }
    }

    fun Button.pressing(callback: () -> Unit) {
        buttons[this] = SuperGamepadDslButtonBuilder {
            pressing(callback)
        }
    }

    fun Button.released(callback: () -> Unit) {
        buttons[this] = SuperGamepadDslButtonBuilder  {
            pressed(callback)
        }
    }

    fun build() {
        block()

        superGamepad.registerEvent(object: SuperGamepadEvent() {
            override fun buttonsPressed(buttons: Buttons) {
                this@SuperGamepadDslBuilder.buttons.forEach {
                    if(buttons(it.key))
                        it.value.pressedCallback?.invoke()
                }
            }

            override fun buttonsBeingPressed(buttons: Buttons) {
                this@SuperGamepadDslBuilder.buttons.forEach {
                    if(buttons(it.key))
                        it.value.pressingCallback?.invoke()
                }
            }

            override fun buttonsReleased(buttons: Buttons) {
                this@SuperGamepadDslBuilder.buttons.forEach {
                    if(buttons(it.key))
                        it.value.releasedCallback?.invoke()
                }
            }
        })
    }

}