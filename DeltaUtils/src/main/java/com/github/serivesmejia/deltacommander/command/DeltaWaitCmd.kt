package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.qualcomm.robotcore.util.ElapsedTime

class DeltaWaitCmd(
    val timeSeconds: Double,
    val timer: ElapsedTime = ElapsedTime()
) : DeltaWaitConditionCmd({ timer.seconds() >= timeSeconds }) {

    override fun init() = timer.reset()

}

open class DeltaWaitConditionCmd(val condition: () -> Boolean): DeltaCommand() {

    override fun run() {
        if(condition()) requestFinish()
    }

}