package com.github.serivesmejia.deltacontrol

import com.github.serivesmejia.deltamath.DeltaMathUtil
import com.qualcomm.robotcore.util.ElapsedTime

class MotorPIDFController(private var coeffs: PIDFCoefficients) {

    constructor(coeffs: com.qualcomm.robotcore.hardware.PIDFCoefficients) : this(
            PIDFCoefficients(coeffs.p, coeffs.i, coeffs.d, coeffs.f)
    )

    private var inverse = false

    //temp values params

    private var errorTolerance = 0.0
    private var deadZone = 0.0

    private var setpoint = 0.0

    private var prevErrorDelta = 0.0
    private var prevMillis = 0.0
    private var prevInput = -1.0

    private var velocityDelta = 1.0
    private var errorDelta = errorTolerance + 1
    private var totalError = 0.0

    private var pidMultiplier = 1.0
    private var invertError = false

    private var initialPower = 1.0

    private var firstLoop = true

    private val elapsedTime = ElapsedTime()

    fun setCoefficients(pid: PIDFCoefficients) {
        this.coeffs = pid
    }

    fun setSetpoint(setpoint: Double) : MotorPIDFController {
        this.setpoint = setpoint;
        return this
    }

    fun setErrorTolerance(errorTolerance: Double) : MotorPIDFController {
        this.errorTolerance = errorTolerance
        return this
    }

    fun setDeadzone(deadzone: Double) : MotorPIDFController {
        this.deadZone = deadzone
        return this
    }

    fun setPIDMultiplier(multiplier: Double) : MotorPIDFController {
        pidMultiplier = multiplier
        return this
    }

    fun setErrorInverted() : MotorPIDFController {
        invertError = !invertError
        return this
    }

    fun setErrorInverted(inverted: Boolean) : MotorPIDFController {
        invertError = inverted
        return this
    }

    fun setInverse() : MotorPIDFController {
        inverse = !inverse
        return this
    }

    fun setInitialPower(initialPower: Double) : MotorPIDFController {
        this.initialPower = initialPower
        return this
    }

    fun getPID() = coeffs

    fun getCurrentError() = errorDelta

    fun getSetpoint() = setpoint

    fun onSetpoint(): Boolean {
        return if(invertError) {
            (prevErrorDelta > -errorTolerance) && prevErrorDelta < 0
        } else {
            (prevErrorDelta < errorTolerance) && prevErrorDelta > 0
        }
    }

    /**
     * Calculate the output with a given input
     * @return the output
     */
    fun calculate(input: Double) : Double {
        if(firstLoop) {
            errorDelta = errorTolerance + 1
            firstLoop = false
        }

        errorDelta = if(invertError)
            input - setpoint
        else
            setpoint - input

        velocityDelta = (errorDelta - prevErrorDelta) / elapsedTime.seconds()
        totalError += errorDelta * elapsedTime.seconds()

        val proportional = errorDelta * coeffs.kP * pidMultiplier
        val integral = totalError * coeffs.kI * pidMultiplier
        val derivative = velocityDelta * coeffs.kD * pidMultiplier
        val feedforward = setpoint * coeffs.kF * pidMultiplier

        val turbo = proportional + integral + derivative + feedforward

        var powerF = initialPower * turbo

        if (powerF > 0) {
            powerF = DeltaMathUtil.clamp(powerF, deadZone, 1.0)
        } else if (powerF < 0) {
            powerF = DeltaMathUtil.clamp(powerF, -1.0, -deadZone)
        }

        prevErrorDelta = errorDelta
        prevInput = input

        elapsedTime.reset()

        return if(inverse) {
            if(onSetpoint())
                initialPower
            else initialPower - powerF
        } else powerF
    }

    /**
     * Resets all values to default in order to start a different PID Loop
     */
    fun reset() : MotorPIDFController {
        errorTolerance = 0.0
        deadZone = 0.0

        setpoint = 0.0

        prevErrorDelta = 0.0
        prevMillis = 0.0
        prevInput = -1.0

        velocityDelta = 1.0
        errorDelta = errorTolerance + 1
        totalError = 0.0

        pidMultiplier = 1.0
        invertError = false

        firstLoop = true

        return this
    }

}
