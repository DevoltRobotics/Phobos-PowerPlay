package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand

// a command that does nothing
object NoCmd : DeltaCommand() {
    override fun run() {
    }
}