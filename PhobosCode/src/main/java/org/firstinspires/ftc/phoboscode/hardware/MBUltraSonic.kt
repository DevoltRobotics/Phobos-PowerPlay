package org.firstinspires.ftc.phoboscode.hardware

import com.qualcomm.robotcore.hardware.AnalogInput

class MBUltraSonic(private val input: AnalogInput) {

    var voltageRatio = (3.3 / input.maxVoltage)

    val distance get() = (input.voltage / (0.185 * voltageRatio)) * 13.25

}