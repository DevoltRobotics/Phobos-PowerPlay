package com.github.serivesmejia.deltacommander

fun interface DeltaSchedulerEvent {

    fun run(command: DeltaCommand)

}