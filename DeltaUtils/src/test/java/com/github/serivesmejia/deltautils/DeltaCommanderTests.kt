package com.github.serivesmejia.deltautils

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltacommander.reset
import com.qualcomm.robotcore.util.ElapsedTime
import org.junit.Test

class DeltaCommanderTests {

    class TestSubsystem : DeltaSubsystem() {
        var commandIndex = 0

        override fun loop() { }
    }

    class TestCommandA : DeltaCommand() {
        val timer = ElapsedTime()

        val sub = require<TestSubsystem>()
        val index = sub.commandIndex

        override fun init() {
            println("init command $index")
            sub.commandIndex++

            timer.reset()
        }

        var scheduledOther = false

        override fun run() {
            if(timer.seconds() >= 5 && !scheduledOther) {
                + TestCommandA()
                println("scheduled other $index")
                scheduledOther = true
            }
        }

        override fun endCondition() = timer.seconds() >= 10

        override fun ending() {
            println("ending $index (${timer.seconds()}s)")
        }

        override fun end(interrupted: Boolean) {
            println("ended $index (${timer.seconds()}s)")
        }
    }

    fun resetScheduler() {
        deltaScheduler.reset()
    }

    //@Test
    fun `Test Scheduler Waiting Commands`() {
        resetScheduler()

        TestSubsystem()
        + TestCommandA()

        deltaScheduler.updateUntilNoCommands()
    }

}