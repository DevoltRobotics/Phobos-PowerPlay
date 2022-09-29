package com.github.serivesmejia.deltadrive.motors

import com.github.serivesmejia.deltadrive.utils.gear.TwoGearRatio

interface MotorData {
    val TICKS_PER_REVOLUTION: Double
    val NO_LOAD_RPM: Double
    val GEAR_RATIO: TwoGearRatio
}