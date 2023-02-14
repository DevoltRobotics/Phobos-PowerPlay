package org.firstinspires.ftc.phoboscode.command.turret

import com.acmerobotics.roadrunner.profile.MotionProfile
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator
import com.acmerobotics.roadrunner.profile.MotionState
import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.deltaScheduler
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.phoboscode.subsystem.Turret
import org.firstinspires.ftc.phoboscode.subsystem.TurretSubsystem
import kotlin.math.sign

open class TurretMoveToAngleCmd(var angle: Double, val endOnTargetReached: Boolean = false) : DeltaCommand() {

    val sub = require<TurretSubsystem>()

    lateinit var motionProfile: MotionProfile
        private set

    val timer = ElapsedTime()

    override fun init() {
        sub.controller.reset()

        motionProfile = generateProfile()
        sub.recreateController()
    }

    override fun run() {

        val t = timer.seconds()

        val state = motionProfile[t]

        sub.controller.targetPosition = state.x
        sub.controller.targetVelocity = state.v
        sub.controller.targetAcceleration = state.a

        val voltage = sub.voltageSensor.voltage

        sub.motor.power = (sub.controller.update(sub.motor.currentPosition.toDouble(), sub.motor.velocity) * 0.9) * (12.0 / voltage)

        if(endOnTargetReached && !sub.isOnTarget) {
            deltaScheduler.end(this)
        }
    }

    override fun end(interrupted: Boolean) {
        sub.motor.power = 0.0
    }

    private fun generateProfile() = MotionProfileGenerator.generateSimpleMotionProfile(
        MotionState(sub.motor.currentPosition.toDouble(), 0.0, 0.0),
        MotionState(angle * Turret.ticksPerAngle, 0.0, 0.0),
        Turret.maxDegreesPerSecond * Turret.ticksPerAngle,
        Turret.maxDegreesPerSecondPerSecond * Turret.ticksPerAngle
    )

}