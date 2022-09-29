package com.github.serivesmejia.deltautils

import com.github.serivesmejia.deltacontrol.PIDFCoefficients
import com.github.serivesmejia.deltacontrol.MotorPIDFController
import org.junit.Assert.*
import org.junit.Test

class DeltaPIDControllerTests {

    val coeffs = PIDFCoefficients(0.08, 0.0005, 0.0003)

    @Test
    fun testPIDControllerOutput() {
        var currDeg = 0.0
        var onSetpoint = false

        TestUtil.spawnTimeoutThread({
            val pidController = MotorPIDFController(coeffs)

            pidController.setSetpoint(90.0)
                    .setDeadzone(0.1)
                    .setInitialPower(1.0)
                    .setErrorTolerance(1.0)

            while(!pidController.onSetpoint() && !Thread.currentThread().isInterrupted) {
                val powerF = pidController.calculate(currDeg)
                currDeg += powerF * (Math.random() * 10) - (Math.random() * 2)
                onSetpoint = pidController.onSetpoint()

                println("Power: $powerF, Sim. degrees: $currDeg")

                Thread.sleep(20)
            }

            if(Thread.currentThread().isInterrupted) {
                println("Timeout!")
            }

        }, 10.0)

        println("Final simulated degrees: $currDeg")

        assertTrue(onSetpoint)
    }

    @Test
    fun testPIDControllerOutputInverted() {
        var currDeg = -90.0
        var onSetpoint = false

        TestUtil.spawnTimeoutThread({

            val pidController = MotorPIDFController(coeffs)

            pidController.setSetpoint(90.0)
                    .setDeadzone(0.1)
                    .setInitialPower(1.0)
                    .setErrorTolerance(1.0)
                    .setErrorInverted()

            while(!pidController.onSetpoint() && !Thread.currentThread().isInterrupted) {
                val powerF = pidController.calculate(currDeg);
                currDeg += powerF * 0.34
                onSetpoint = pidController.onSetpoint()

                println("Power: $powerF, Sim. degrees: $currDeg")

                Thread.sleep(20)
            }

            if(Thread.currentThread().isInterrupted) {
                println("Timeout!")
            }

        }, 20.0)

        println("Final simulated degrees: $currDeg")

        assertTrue(onSetpoint)
    }

}