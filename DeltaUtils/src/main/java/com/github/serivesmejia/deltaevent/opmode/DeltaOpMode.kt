package com.github.serivesmejia.deltaevent.opmode

import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltacommander.reset
import com.github.serivesmejia.deltaevent.gamepad.SuperGamepad
import com.github.serivesmejia.deltasimple.SimpleHardware
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

abstract class DeltaOpMode : LinearOpMode() {

    lateinit var superGamepad1: SuperGamepad
    lateinit var superGamepad2: SuperGamepad

    abstract val hardware: SimpleHardware

    private var isDefaultRun = false

    override fun runOpMode() {
        val lynxModules = hardwareMap.getAll(LynxModule::class.java)

        deltaScheduler.reset()

        superGamepad1 = SuperGamepad(gamepad1)
        superGamepad2 = if(gamepad1 != gamepad2) {
            SuperGamepad(gamepad2)
        } else {
            superGamepad1
        }

        hardware.initHardware(hardwareMap)

        initialize()

        superGamepad1.attachToScheduler()
        superGamepad2.attachToScheduler()

        while(!isStarted && !isStopRequested) {
            initializeUpdate()
        }

        if(isStopRequested) return

        begin()

        for(module in lynxModules) {
            module.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        }

        while(opModeIsActive()) {
            for(module in lynxModules) {
                module.clearBulkCache()
            }

            runUpdate()

            deltaScheduler.update()

            if(isDefaultRun && deltaScheduler.commandsAmount <= 0) {
                break
            }
        }
    }

    abstract fun initialize()

    open fun initializeUpdate() { }

    open fun begin() { }

    open fun runUpdate() {
        isDefaultRun = true
    }

}