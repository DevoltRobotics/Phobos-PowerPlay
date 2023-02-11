package org.firstinspires.ftc.phoboscode.hardware

import com.acmerobotics.roadrunner.localization.Localizer
import org.firstinspires.ftc.phoboscode.Side
import org.firstinspires.ftc.phoboscode.Side.*

class UltraSonicRelocalizer(val left: MBUltraSonic, val right: MBUltraSonic, var side: Side) {

    companion object {
        val sensorToCenterInchesOffset = 7.0
    }

    val relevantDistance get() = when(side) {
        LEFT -> left.distance
        RIGHT -> right.distance
    }

    val xEstimate get() = when(side) {
        LEFT -> -(70 - (relevantDistance + sensorToCenterInchesOffset))
        RIGHT -> 70 - (relevantDistance - sensorToCenterInchesOffset)
    }

    fun relocalize(localizer: Localizer, offset: Double = 0.0) {
        localizer.poseEstimate = localizer.poseEstimate.copy(x = xEstimate + offset)
    }

}