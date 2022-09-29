package com.github.serivesmejia.deltasimple

import com.qualcomm.robotcore.hardware.HardwareMap

abstract class SimpleHardware {

    lateinit var hardwareMap: HardwareMap

    fun initHardware(hardwareMap: HardwareMap) {
        this.hardwareMap = hardwareMap
        init()
    }
    
    protected open fun init() { }

    inline fun <reified T> hardware(name: String): Lazy<T> = lazy {
        require(::hardwareMap.isInitialized) { "The HardwareMap is not defined, call initHardware(hardwareMap)" }
        hardwareMap.get(T::class.java, name)!!
    }


}