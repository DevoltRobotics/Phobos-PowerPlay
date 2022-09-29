package com.github.serivesmejia.deltasimple.motor

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.abs
import kotlin.math.sign

class SimpleEncoder(val motorEx: DcMotorEx, val ticksPerRev: Double, val multiplier: Double = 1.0) {

    private var lastPosition = 0.0
    private var velocityEstimate = 0.0
    private var lastUpdateTime = 0.0

    private val elapsedTime = ElapsedTime()

    init {
        motorEx.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    val position: Double
        get() {
            val position = motorEx.currentPosition * multiplier

            //calculating an estimated velo for fixing overflow
            //on encoders such as REV's through bore
            if(position != lastPosition) {
                val currTime = elapsedTime.seconds()
                val deltaTime = currTime - lastUpdateTime

                velocityEstimate = (position - lastPosition) / deltaTime

                lastPosition = position
                lastUpdateTime = currTime
            }

            return position
        }

    val positionRevs: Double
        get() { return position / ticksPerRev }

    val velocity: Double
        get() { return motorEx.velocity * multiplier }

    val velocityRevs: Double
        get() { return velocity / ticksPerRev }

    val correctedVelocity: Double
        get() { return inverseOverflow(velocity, velocityEstimate) }

    val correctedVelocityRevs: Double
        get() { return correctedVelocity / ticksPerRev }

}

const val CPS_STEP = 0x10000

private fun inverseOverflow(input: Double, estimate: Double): Double {
    var real = input
    if(abs(estimate - real) > CPS_STEP / 2.0) {
        real += sign(estimate - real) * CPS_STEP;
    }
    return real
}