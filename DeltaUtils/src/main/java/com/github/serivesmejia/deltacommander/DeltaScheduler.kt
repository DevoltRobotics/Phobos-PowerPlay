package com.github.serivesmejia.deltacommander

@Suppress("UNUSED")
class DeltaScheduler internal constructor() {

    var enabled = true

    //hashmap containing the subsystems and their default commands
    private val addedSubsystems = mutableMapOf<DeltaSubsystem, DeltaCommand?>()

    val subsystems get() = addedSubsystems.keys.toTypedArray()

    //hashmap containing the currently scheduled commands and their state
    private val scheduledCommands = mutableMapOf<DeltaCommand, DeltaCommand.State>()

    private val commandScheduleQueue = mutableListOf<QueuedCommand>()
    val queuedCommands get() = commandScheduleQueue.map { it.queuedCommand }.toTypedArray()
    
    val commands get() = scheduledCommands.keys.toTypedArray()
    val commandsAmount get() = scheduledCommands.size

    //hashmap containing the required subsystems by specific commands
    private val requirements: HashMap<DeltaSubsystem, MutableList<DeltaCommand>> = HashMap()

    //user events
    private val initEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val runEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val interruptEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val endEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val runSchedulerEvents: ArrayList<Runnable> = ArrayList()

    /**
     * Schedule a command to be runned
     * @param cmd the command to be scheduled
     * @param isInterruptible whether the command can be interrupted
     */
    fun schedule(cmd: DeltaCommand, isInterruptible: Boolean = true): Boolean {
        if(!enabled) return false
        if(scheduledCommands.contains(cmd)) return false

        val cmdReqs = cmd.requirements
        var reqsCurrentlyInUse = false

        //check if a requirement from the scheduled command is currently in use
        for(req in cmdReqs) {
            reqsCurrentlyInUse = requirements.contains(req)
            if(reqsCurrentlyInUse) break
        }

        if(!reqsCurrentlyInUse) {
            addCommand(cmd, isInterruptible) //directly run it, if none of its requirements are in use
            return true
        } else {
            val waitingCommands = mutableListOf<DeltaCommand>()

            for(req in cmdReqs) {
                if(requirements.containsKey(req)) {
                    val runningCommands = requirements[req]!!.size

                    // if the subsystem is not at it's max capacity of running commands,
                    // schedule the requested command and move on
                    if(runningCommands < req.maxRunningCommands) {
                        addCommand(cmd, isInterruptible)
                        return true
                    } else {
                        for (runningCmd in requirements[req]!!) {
                            //check if the commands requiring a specific subsystem are interruptible
                            if (!scheduledCommands[runningCmd]!!.interruptible) {
                                return false
                            } else {
                                if (!runningCmd.isActive()) {
                                    if (!runningCmd.endingCalled) {
                                        runningCmd.ending()
                                        runningCmd.endingCalled = true
                                    }
                                    runningCmd.finishRequested = true

                                    waitingCommands.add(runningCmd)
                                }
                            }
                        }
                    }
                }
            }

            if(waitingCommands.isNotEmpty()) {
                commandScheduleQueue.add(QueuedCommand(cmd, isInterruptible, waitingCommands))
                return true
            }

            //cancel all the commands that require a subsystem
            for(req in cmdReqs) {
                if(requirements.containsKey(req)) {
                    requirements[req]!!.forEach(this::stop)
                }
            }

            addCommand(cmd, isInterruptible) //schedule the command once all the other requiring commands were cancelled
            return true
        }
    }

    private fun addCommand(cmd: DeltaCommand, isInterruptible: Boolean) {
        cmd.finishRequested = false
        cmd.endingCalled = false
        cmd.allowRequire = false
        cmd.hasRunOnce = false
        cmd.init()

        val state = DeltaCommand.State(isInterruptible)

        for(req in cmd.requirements) {
            if(!requirements.containsKey(req)) {
                requirements[req] = mutableListOf()
            }

            requirements[req]!!.add(cmd)
        }

        scheduledCommands[cmd] = state

        for(evt in initEvents) { evt.run(cmd) } //run the init user events
    }

    /**
     * Schedule multiple commands
     * @param cmds multiple commands to be scheduled
     * @param isInterruptible whether the commands can be interrupted
     */
    fun schedule(isInterruptible: Boolean = true, vararg cmds: DeltaCommand) {
        for(cmd in cmds) {
            schedule(cmd, isInterruptible)
        }
    }

    /**
     * Add one or multiple subsystems
     * @param subsystems multiple subsystems to be scheduled
     */
    fun addSubsystem(vararg subsystems: DeltaSubsystem) {
        for(subsystem in subsystems) {
            this.addedSubsystems[subsystem] = null
        }
    }

    /**
     * Remove one or multiple subsystems
     * @param subsystems multiple subsystems to be removed
     */
    fun removeSubsystem(vararg subsystems: DeltaSubsystem) {
        for(subsystem in subsystems) {
            this.addedSubsystems.remove(subsystem)
        }
    }

    /**
     * Run all the scheduled commands & events
     */
    fun update() {
        if(!enabled) return //if the schedulers is disabled then abort

        for(subsystem in addedSubsystems.keys) { subsystem.internalUpdate() } //run the loop method of all the subsystems

        queuedCommandsLoop@
        for(queuedCommand in commandScheduleQueue.toTypedArray()) {
            for(waitingCommand in queuedCommand.waitingCommands) {
                if(!waitingCommand.isActive() && !waitingCommand.finishRequested)
                    continue@queuedCommandsLoop
            }

            addCommand(queuedCommand.queuedCommand, queuedCommand.isInterruptible)
            commandScheduleQueue.remove(queuedCommand)
        }

        for((cmd, _) in scheduledCommands.entries.toTypedArray()) { //iterate through the scheduled commands
            cmd.run() //actually run the command
            cmd.hasRunOnce = true

            for(evt in runEvents) { evt.run(cmd) } //execute the user events

            if(cmd.isActive() && cmd.finishRequested) { //end and remove the command if it's finished
                stop(cmd)
            }
        }

        //register default command if no command is requiring the subsystem
        for((subsystem, defCmd) in addedSubsystems) {
            if(!requirements.containsKey(subsystem) && defCmd != null) {
                schedule(true, defCmd) //schedule the default command if no other command is scheduled for this subsystem
            }
        }

        for(evt in runSchedulerEvents) { evt.run() }
    }

    fun updateUntilNoCommands(updateCallback: () -> Unit = { }) {
        while(scheduledCommands.isNotEmpty() && !Thread.currentThread().isInterrupted) {
            updateCallback()
            update()
        }
    }

    /**
     * Sets the default command for the specified subsystem, which will run
     * when no other command is running requiring the subsystem.
     * @param subsystem the subsystem to set the default command
     * @param command the default command
     */
    fun setDefaultCommand(subsystem: DeltaSubsystem, command: DeltaCommand) {
        if(!command.requirements.contains(subsystem)) {
            throw IllegalArgumentException("Default command \"${command.name}\" does not require its subsystem \"${subsystem.name}\"")
        }

        if(command.finishRequested) {
            throw IllegalArgumentException("Default command \"${command.name}\" is finished")
        }

        addedSubsystems[subsystem] = command
    }

    /**
     * Get the default command of a specified subsystem
     * @param subsystem the subsystem to get the default command from
     */
    fun getDefaultCommand(subsystem: DeltaSubsystem) = addedSubsystems[subsystem]

    /**
     * Check if a command is scheduled
     */
    fun isScheduled(command: DeltaCommand) = scheduledCommands.containsKey(command)

    /**
     * Stop one or more commands
     * @param cmds commands to be stopped
     */
    fun stop(vararg cmds: DeltaCommand) {
        for(cmd in cmds) {
            if(!scheduledCommands.containsKey(cmd)) continue
            if(!cmd.endingCalled) {
                cmd.endingCalled = true
                cmd.ending()
            }

            cmd.end(true)

            for(evt in interruptEvents) { evt.run(cmd) }

            scheduledCommands.remove(cmd)
            requirements.keys.removeAll(cmd.requirements)

            cmd.allowRequire = true
        }
    }

    /**
     * Stop all the currently requested commands
     */
    fun stopAll() {
        for((cmd, _) in scheduledCommands) {
            stop(cmd)
        }
    }

    /**
     * Get the command requiring a subsystem.
     * Will return null if not currently required.
     */
    fun requirements(subsystem: DeltaSubsystem) = requirements[subsystem]

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    //Event register methods

    fun onInitCommand(event: DeltaSchedulerEvent) = initEvents.add(event)

    fun onRunCommand(event: DeltaSchedulerEvent) = runEvents.add(event)

    fun onInterruptCommand(event: DeltaSchedulerEvent) = interruptEvents.add(event)

    fun onEndCommand(event: DeltaSchedulerEvent) = endEvents.add(event)

    fun onRunScheduler(event: Runnable) = runSchedulerEvents.add(event)

    private data class QueuedCommand(val queuedCommand: DeltaCommand,
                                     val isInterruptible: Boolean,
                                     val waitingCommands: MutableList<DeltaCommand>)

}

//actual variable containing the singleton
private var inst: DeltaScheduler? = null

//variable which will be accessed statically
val deltaScheduler: DeltaScheduler
    get() {
        if(inst == null) inst = DeltaScheduler()
        return inst!!
    }

/**
 * Destroys the current scheduler instance
 * A new one will be created the next time the variable is accessed
 */
fun DeltaScheduler.reset() {
    inst = null
}