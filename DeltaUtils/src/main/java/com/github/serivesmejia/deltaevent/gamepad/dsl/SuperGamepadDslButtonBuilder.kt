package com.github.serivesmejia.deltaevent.gamepad.dsl

import com.github.serivesmejia.deltaevent.gamepad.button.Button
import java.lang.IllegalStateException

class SuperGamepadDslButtonBuilder(block: SuperGamepadDslButtonBuilder.() -> Unit) {

    internal var pressedCallback: (() -> Unit)? = null
    internal var pressingCallback: (() -> Unit)? = null
    internal var releasedCallback: (() -> Unit)? = null

    init {
        block(this)
    }

    fun pressed(callback: () -> Unit) {
        if(pressedCallback != null)
            throw IllegalStateException("Callback for 'pressed' has been already registered for this button")

        pressedCallback = callback
    }

    fun pressing(callback: () -> Unit) {
        if(pressingCallback != null)
            throw IllegalStateException("Callback for 'pressing' has been already registered for this button")

        pressingCallback = callback
    }

    fun released(callback: () -> Unit) {
        if(releasedCallback != null)
            throw IllegalStateException("Callback for 'released' has been already registered for this button")

        releasedCallback = callback
    }

}