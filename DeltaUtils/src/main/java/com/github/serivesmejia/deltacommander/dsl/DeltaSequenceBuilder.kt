package com.github.serivesmejia.deltacommander.dsl

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.command.DeltaInstantCmd
import com.github.serivesmejia.deltacommander.command.DeltaSequentialCmd
import com.github.serivesmejia.deltacommander.command.DeltaWaitCmd
import com.github.serivesmejia.deltacommander.command.DeltaWaitConditionCmd
import com.github.serivesmejia.deltadrive.utils.task.Task

class DeltaSequenceBuilder(private val block: DeltaSequenceBuilder.() -> Unit) {

    private val commands = mutableListOf<DeltaCommand>()

    private val requirements = mutableListOf<DeltaSubsystem>()

    operator fun <T : DeltaCommand> T.unaryMinus(): T {
        commands.add(this)
        return this
    }

    operator fun <T : Task<*>> T.unaryMinus(): T {
        commands.add(this.command)
        return this
    }

    fun DeltaCommand.dontBlock() = DeltaInstantCmd(this::schedule)

    fun Task<*>.dontBlock() = DeltaInstantCmd(this.command::schedule)

    fun waitFor(condition: () -> Boolean) = DeltaWaitConditionCmd(condition)

    fun waitForSeconds(seconds: Double) = DeltaWaitCmd(seconds)

    fun require(subsystem: DeltaSubsystem) = requirements.add(subsystem)

    inline fun <reified C: DeltaCommand> C.stopOn(noinline condition: C.() -> Boolean): DeltaCommand {
        val command = this

        - deltaSequence {
            - waitFor { command.hasRunOnce && condition(command) }
            - DeltaInstantCmd(command::requestFinish)
        }.dontBlock()

        return this
    }

    internal fun build(): DeltaSequentialCmd {
        block()
        return DeltaSequentialCmd(*commands.toTypedArray()).apply {
            require(*requirements.toTypedArray())
        }
    }

}

fun deltaSequence(block: DeltaSequenceBuilder.() -> Unit) = DeltaSequenceBuilder(block).build()