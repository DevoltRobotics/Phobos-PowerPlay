package com.github.serivesmejia.deltasimple.sensor

import com.github.serivesmejia.LibraryData
import com.github.serivesmejia.deltadrive.utils.Axis
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.qualcomm.hardware.bosch.BNO055IMU
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation

@Suppress("UNUSED")
class SimpleBNO055IMU(private val imu: BNO055IMU) {

    var initialized = false
        private set

    private var lastAngles = Orientation()
    private var globalAngle = 0.0

    var axis = Axis.Z

    fun initIMU() {
        if(initialized) return

        val param = BNO055IMU.Parameters()

        param.mode = BNO055IMU.SensorMode.IMU
        param.angleUnit = BNO055IMU.AngleUnit.DEGREES
        param.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        param.loggingEnabled = false

        imu.initialize(param)
        initialized = true
    }

    /**
     * Enter in a while loop until the IMU reports it is calibrated or until the opmode stops
     */
    fun waitForIMUCalibration(telemetry: Telemetry) {
        while (!imu.isGyroCalibrated && !Thread.interrupted()) {
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...")
            telemetry.addData("[Status]", "$imuCalibrationStatus\n\nDeltaUtils v${LibraryData.VERSION}")
            telemetry.update()
        }
    }

    /**
     * Enter in a while loop until the IMU reports it is calibrated or until the opmode stops
     */
    fun waitForIMUCalibration() {
        while (!imu.isGyroCalibrated && !Thread.interrupted()) { }
    }

    /**
     * The IMU calibration status as a String
     */
    val imuCalibrationStatus get() = imu.calibrationStatus.toString()

    val isImuCalibrated get() = imu.isGyroCalibrated

    val cumulativeAngle: Rot2d get() {
        // We have to process the angle because the imu works in euler angles so the axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.
        val angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)

        var deltaAngle = when (axis) {
            Axis.X -> angles.thirdAngle - lastAngles.thirdAngle
            Axis.Y -> angles.secondAngle - lastAngles.secondAngle
            else -> angles.firstAngle - lastAngles.firstAngle
        }

        if (deltaAngle < -180)
            deltaAngle += 360
        else if (deltaAngle > 180)
            deltaAngle -= 360

        globalAngle += deltaAngle
        lastAngles = angles

        lastCumulativeAngle = Rot2d.degrees(globalAngle)

        return lastCumulativeAngle
    }

    var lastCumulativeAngle = Rot2d()

    fun resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
        globalAngle = 0.0
    }

}