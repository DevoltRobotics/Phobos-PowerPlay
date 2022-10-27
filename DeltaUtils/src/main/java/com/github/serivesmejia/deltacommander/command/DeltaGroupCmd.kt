@file:Suppress("UNUSED")

package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand

open class DeltaGroupCmd(private val executionMode: ExecutionMode = ExecutionMode.PARALLEL, vararg commands: DeltaCommand) : DeltaCommand() {

    var commands: ArrayList<DeltaCommand> = ArrayList()

    var currentCommandIndex = 0

    init {
        if(commands.isEmpty()) {
            throw IllegalArgumentException("You should provide one or more commands to the GroupedCommand")
        }

        //and add all the commands from the vararg to the arraylist
        for(cmd in commands) {
            this.commands.add(cmd)

            // add their requirements too
            for(requirement in cmd.requirements) {
                if(!requirements.contains(requirement)) {
                    require(requirement)
                }
            }
        }
    }

    private var currentCommand: DeltaCommand? = null

    override fun init() {
        currentCommand = null
        currentCommandIndex = 0

        if(executionMode == ExecutionMode.PARALLEL) {
            for(cmd in commands) {
                cmd.schedule()
            }
        }
    }

    override fun run() {
        when(executionMode) {
            //execute commands in linear mode, which will run one command at a time sequentially until
            //all the commands are finished, which the grouped command (this) will also be finished
            ExecutionMode.LINEAR -> {
                if(!commands[currentCommandIndex].isScheduled) {
                    if(currentCommand != null) currentCommandIndex++

                    if(currentCommandIndex >= commands.size) {
                        requestEnd()
                        return
                    }

                    currentCommand = commands[currentCommandIndex]
                    currentCommand!!.schedule()
                }
            }

            //execute commands in parallel mode, which will run all the commands at once until they're all finished
            //if all the subcommands are finished, the grouped command (this) will also finish
            ExecutionMode.PARALLEL -> {
                var finishedCount = 0

                for(cmd in commands) {
                    if(!cmd.isScheduled) {
                        finishedCount++
                    }
                }

                if(finishedCount >= commands.size) {
                    requestEnd()
                }
            }
        }
    }

    override fun end(interrupted: Boolean) {
        for(cmd in commands) {
            if(!cmd.endRequested) cmd.end(interrupted)
        }
    }

}

enum class ExecutionMode { LINEAR, PARALLEL }

open class DeltaSequentialCmd(
        vararg commands: DeltaCommand
) : DeltaGroupCmd(ExecutionMode.LINEAR, *commands)

class DeltaParallelCmd(
        vararg commands: DeltaCommand
) : DeltaGroupCmd(ExecutionMode.PARALLEL, *commands)