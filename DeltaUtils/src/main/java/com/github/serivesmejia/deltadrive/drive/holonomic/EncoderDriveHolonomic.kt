/*
 * Copyright (c) 2020 FTC Delta Robotics #9351 - Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.serivesmejia.deltadrive.drive.holonomic

import com.github.serivesmejia.deltacontrol.MotorPIDFController
import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltadrive.utils.DistanceUnit
import com.github.serivesmejia.deltadrive.utils.task.Task
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltasimple.sensor.SimpleBNO055IMU
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("UNUSED")
class EncoderDriveHolonomic
/**
 * Constructor for the encoder drive class
 * @param hdw The initialized hardware containing all the chassis motors
 * @param telemetry The current OpMode telemetry to show movement info.
 * @param parameters Encoder parameters, in order to calculate the ticks per inch for each motor
 */
(private val hdw: DeltaHardwareHolonomic,
 private var parameters: EncoderDriveParameters,
 private val telemetry: Telemetry? = null,
 var imu: SimpleBNO055IMU? = null) {

    private val runtime = ElapsedTime()

    init {
        hdw.runMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        hdw.runMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    private fun encoderDrive(
            speed: Double,
            frontleft: Double, frontright: Double,  backleft: Double, backright: Double,
            timeoutS: Double,
            rightTurbo: Double, leftTurbo: Double,
            movementDescription: String, correctWithIMU: Boolean = true) : Task<Unit> {

        var fl = frontleft
        var fr = frontright
        var bl = backleft
        var br = backright

        parameters.secureParameters()

        if (parameters.DISTANCE_UNIT === DistanceUnit.CENTIMETERS) {
            fl *= 0.393701
            fr *= 0.393701
            bl *= 0.393701
            br *= 0.393701
        }

        val beforeRunMode = hdw.runMode

        val leftPower = abs(speed) * leftTurbo
        val rightPower = abs(speed) * rightTurbo

        var initialAngle = Rot2d.zero

        var flPow = if(fl != 0.0) leftPower else 0.0
        var frPow = if(fr != 0.0) rightPower else 0.0
        var blPow = if(bl != 0.0) leftPower else 0.0
        var brPow = if(br != 0.0) rightPower else 0.0

        val controllerLeft = MotorPIDFController(parameters.DRIVE_STRAIGHT_COEFFICIENTS)
        val controllerRight = MotorPIDFController(parameters.DRIVE_STRAIGHT_COEFFICIENTS)

        var newFrontLeftTarget = 0
        var newFrontRightTarget = 0
        var newBackLeftTarget = 0
        var newBackRightTarget = 0

        return Task(parameters.TASK_COMMAND_REQUIREMENTS) {
            first {
                // Determine new target position, and pass to motor controller
                val ticksPerInch = calcTicksPerInch()
                newFrontLeftTarget = (hdw.wheelFrontLeft.currentPosition + (fl * ticksPerInch)).roundToInt()
                newFrontRightTarget = (hdw.wheelFrontRight.currentPosition + (fr * ticksPerInch)).roundToInt()
                newBackLeftTarget = (hdw.wheelBackLeft.currentPosition + (bl * ticksPerInch)).roundToInt()
                newBackRightTarget = (hdw.wheelBackRight.currentPosition + (br * ticksPerInch)).roundToInt()

                // reset the timeout time and start motion.
                runtime.reset()

                if(imu != null && correctWithIMU) {
                    initialAngle = imu!!.cumulativeAngle

                    controllerLeft.setSetpoint(initialAngle.degrees)
                              .setInitialPower(speed).setInverse()
                              .setErrorTolerance(parameters.DRIVE_STRAIGHT_DEGREE_TOLERANCE)
                              .setDeadzone(parameters.DRIVE_STRAIGHT_DEADZONE)

                    controllerRight.setSetpoint(initialAngle.degrees)
                                   .setInitialPower(speed).setInverse()
                                   .setErrorTolerance(parameters.DRIVE_STRAIGHT_DEGREE_TOLERANCE)
                                   .setDeadzone(parameters.DRIVE_STRAIGHT_DEADZONE)
                                   .setErrorInverted()
                }
            }

            var (dFl, dFr, dBl, dBr) = Distances(0, 0, 0, 0)

            if(parameters.SHOW_CURRENT_DISTANCE || markers.distanceMarkersCount > 0) {
                dFl = abs(hdw.wheelFrontLeft.currentPosition)
                dFr = abs(hdw.wheelFrontRight.currentPosition)
                dBl = abs(hdw.wheelBackLeft.currentPosition)
                dBr = abs(hdw.wheelBackRight.currentPosition)

                markers.runDistanceMarkers((dFl + dFr + dBl + dBr).toDouble() / 4.0)
            }

            if(parameters.SHOW_CURRENT_DISTANCE) {
                telemetry?.addData("[Movement]", movementDescription)

                telemetry?.addData(
                    "[Current]", "%7d : %7d : %7d : %7d",
                    dFl, dFr, dBl, dBr
                )

                telemetry?.addData("[Target]", "%7d : %7d : %7d : %7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget)
            }

            if(imu != null && correctWithIMU) {
                val powerFLeft = controllerLeft.calculate(imu!!.cumulativeAngle.degrees)
                val powerFRight = controllerRight.calculate(imu!!.lastCumulativeAngle.degrees)

                flPow = if(fl != 0.0) powerFLeft * leftTurbo else 0.0
                frPow = if(fr != 0.0) powerFRight * rightTurbo else 0.0
                blPow = if(bl != 0.0) powerFLeft * leftTurbo else 0.0
                brPow = if(br != 0.0) powerFRight * rightTurbo else 0.0

                telemetry?.addData("[On Setpoint]", "${controllerLeft.onSetpoint()}, ${controllerRight.onSetpoint()}")
                telemetry?.addData("[Angle Error]", "${controllerLeft.getCurrentError()}, ${controllerRight.getCurrentError()}")
                telemetry?.addData("[Power]", "$powerFLeft, $powerFRight")
            }

            telemetry?.update()

            // finish task until there's is no time left or no motors are running.
            // Note: We use (isBusy() && isBusy()) in the repeat test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            if(runtime.seconds() >= timeoutS) { //when it's finished
                // println("helo ending $fl $fr $bl $br") //lol
                telemetry?.update() //clear telemetry

                // Stop all motion
                hdw.setMotorPowers(0.0, 0.0, 0.0, 0.0)
                // Turn off RUN_TO_POSITION
                hdw.runMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
                hdw.runMode = beforeRunMode

                end() //end the task
            }

        }

    }

    fun forward(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)

        return encoderDrive(
            speed, d, d, d, d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "forward"
        )
    }

    fun backwards(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)

        return encoderDrive(
            speed, -d, -d, -d, -d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "backwards"
        )
    }

    fun strafeLeft(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)

        return encoderDrive(
            speed, -d, d, d, -d,
            timeoutS,
            parameters.RIGHT_WHEELS_STRAFE_TURBO, parameters.LEFT_WHEELS_STRAFE_TURBO,
            "strafeLeft"
        )
    }

    fun strafeRight(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
            speed, d, -d, -d, d,
            timeoutS, parameters.RIGHT_WHEELS_STRAFE_TURBO, parameters.LEFT_WHEELS_STRAFE_TURBO, "strafeRight")
    }

    fun turnRight(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
            speed, d, -d, d, -d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "turnRight", false)
    }

    fun turnLeft(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
            speed, -d, d, -d, d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "turnLeft", false
        )
    }

    fun tiltForwardLeft(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
                speed, 0.0, d, d, 0.0,
                timeoutS,
                parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
                "tiltForwardsLeft"
        )
    }

    fun tiltForwardRight(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
                speed, d, 0.0, 0.0, d,
                timeoutS,
                parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
                "tiltForwardsRight"
        )
    }


    fun tiltBackwardsLeft(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
                speed, -d, 0.0, 0.0, -d,
                timeoutS,
                parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
                "tiltBackwardsLeft"
        )
    }

    fun tiltBackwardsRight(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
                speed, 0.0, -d, -d, 0.0,
                timeoutS,
                parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
                "tiltBackwardsRight"
        )
    }

    private fun calcTicksPerInch() =
        parameters.TICKS_PER_REV * parameters.DRIVE_GEAR_REDUCTION.ratioAsDecimal / (parameters.WHEEL_DIAMETER_INCHES * Math.PI)

    data class Distances(var fl: Int, var fr: Int, var bl: Int, var br: Int)

}