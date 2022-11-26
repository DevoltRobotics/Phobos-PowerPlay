package org.firstinspires.ftc.phoboscode.rr.drive

import com.ThermalEquilibrium.homeostasis.Filters.FilterAlgorithms.KalmanFilter
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.hardware.HardwareMap

class KalmanStandardTrackingWheelLocalizer(
    hardwareMap: HardwareMap?
) : StandardTrackingWheelLocalizer(hardwareMap) {

    val kalmanFilter = KalmanFilter(3.0, .3, 3)

    override fun update() {
        super.update()
        val estimate = poseEstimate

        poseEstimate = Pose2d(estimate.x, estimate.y, heading = kalmanFilter.estimate(poseEstimate.heading))
    }

}