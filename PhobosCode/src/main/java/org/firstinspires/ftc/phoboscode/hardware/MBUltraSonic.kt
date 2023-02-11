package org.firstinspires.ftc.phoboscode.hardware

import com.qualcomm.robotcore.hardware.AnalogInput

class MBUltraSonic(private val input: AnalogInput, private val inchesPerVolt: Double) {

    var voltageRatio = 1.0//(3.3 / input.maxVoltage)

    val distance get() = input.voltage * (inchesPerVolt * voltageRatio)

}