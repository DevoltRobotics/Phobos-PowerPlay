package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand

class DeltaRunCmd(private val callback: () -> Unit) : DeltaCommand() {

    override fun run() {
        callback()
    }

}