package com.github.serivesmejia.deltadrive.utils.task

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.command.DeltaTaskCmd
import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.qualcomm.robotcore.util.ElapsedTime

/**
 * Class to represent a task of any sort, from a encoder drive run to position task,
 * to a IMU PID Drive rotate task.
 * @param runn Runnable to be assigned to this task.
 * @param T Type to be returned as a result from the task
 */
@Suppress("UNUSED")
open class Task<T>(
    private val commandRequirements: Array<DeltaSubsystem> = arrayOf(),
    private val runn: Task<T>.() -> T
) {

    var finished = false
        private set

    var result: T? = null
        private set

    val command get() = DeltaTaskCmd(this, *commandRequirements)

    //@Suppress("LeakingThis")
    val markers by lazy { Markers(this) }

    private var hasRan = false

    private val runtime = ElapsedTime()

    open fun run() {
        if(finished) return
        first {
            runtime.reset()
        }

        result = runn(this)
        markers.runTimeMarkers(runtime.seconds())

        hasRan = true
    }

    fun first(callback: () -> Unit) {
        if(!hasRan) callback()
    }

    fun runBlocking() {
        while(!finished) {
            run()
        }
    }

    fun schedule() = deltaScheduler.schedule(command)

    fun end() {
        finished = true
    }

}