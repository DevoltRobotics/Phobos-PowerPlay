package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand

class DeltaInstantCmd(private val callback: () -> Unit) : DeltaCommand() {

    override fun run() {
        callback()
        requestEnd()
    }

}