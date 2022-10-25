package com.github.serivesmejia.deltacommander

import com.github.serivesmejia.deltacommander.command.DeltaInstantCmd
import com.github.serivesmejia.deltacommander.command.DeltaRunCmd
import com.github.serivesmejia.deltacommander.command.DeltaWaitCmd
import com.github.serivesmejia.deltacommander.command.DeltaWaitConditionCmd
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.reflect.KClass

abstract class DeltaCommand {

    val name = this.javaClass.simpleName
    val requirements = mutableListOf<DeltaSubsystem>()

    var finishRequested = false
        internal set
    internal var endingCalled = false

    var hasRunOnce = false
        internal set

    internal var allowRequire = true

    open fun init() {}

    abstract fun run()

    open fun ending() { }

    open fun end(interrupted: Boolean) {}

    fun require(vararg reqs: DeltaSubsystem) {
        if(!allowRequire) {
            throw IllegalStateException("Calling require() is not allowed on this point")
        }

        reqs.forEach {
            if (!requirements.contains(it))
                requirements.add(it)
        }
    }

    open fun isActive() = true

    inline fun <reified S : DeltaSubsystem> require() = require(S::class)

    @Suppress("UNCHECKED_CAST")
    fun <S : DeltaSubsystem> require(clazz: KClass<S>): S {
        for(subsystem in deltaScheduler.subsystems) {
            if(subsystem::class == clazz) {
                require(subsystem)
                return subsystem as S
            }
        }

        throw IllegalArgumentException("Unable to find subsystem ${clazz::class.java.name} in DeltaScheduler")
    }

    @JvmName("requireByName")
    inline fun <reified S : DeltaSubsystem> require(clazzName: String): S {
        val sub = require(clazzName)

        if(sub is S)
            return sub
        else throw IllegalArgumentException("Unable to find subsystem $clazzName with type ${S::class.java.name} in DeltaScheduler")
    }

    fun require(clazzName: String): DeltaSubsystem {
        for(subsystem in deltaScheduler.subsystems) {
            if(subsystem::class.java.simpleName == clazzName) {
                require(subsystem)
                return subsystem
            }
        }

        throw IllegalArgumentException("Unable to find subsystem $clazzName in DeltaScheduler")
    }

    fun requestFinish() {
        finishRequested = true
    }

    fun schedule(isInterruptible: Boolean = true) = deltaScheduler.schedule(this, isInterruptible)

    fun stopAfter(timeSecs: Double): DeltaCommand {
        + deltaSequence {
            - DeltaWaitCmd(timeSecs)
            - DeltaInstantCmd(this@DeltaCommand::requestFinish)
        }

        return this
    }

    fun waitFor() = DeltaWaitConditionCmd(this::isScheduled)

    val isScheduled
        get() = deltaScheduler.commands.contains(this) || deltaScheduler.queuedCommands.contains(this)

    operator fun unaryPlus() = schedule()

    data class State(val interruptible: Boolean)
}

@Suppress("UNCHECKED_CAST")
fun <S : DeltaSubsystem> subsystem(clazz: KClass<S>): S {
    for(subsystem in deltaScheduler.subsystems) {
        if(subsystem::class == clazz) {
            return subsystem as S
        }
    }

    throw IllegalArgumentException("Unable to find subsystem ${clazz::class.java.name} in DeltaScheduler")
}
inline fun <reified S : DeltaSubsystem> subsystem() = subsystem(S::class)

fun DeltaCommand.endRightAway() = deltaSequence {
    - this@endRightAway.dontBlock()
    - DeltaRunCmd {
        this@endRightAway.requestFinish()
    }
}

inline fun <reified C: DeltaCommand> C.stopOn(noinline condition: C.() -> Boolean): DeltaCommand {
    val command = this

    + deltaSequence {
        - waitFor { command.hasRunOnce && condition(command) }
        - DeltaInstantCmd(command::requestFinish)
    }

    return this
}