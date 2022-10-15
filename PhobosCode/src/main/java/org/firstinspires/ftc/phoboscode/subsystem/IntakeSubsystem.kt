package org.firstinspires.ftc.phoboscode.subsystem

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionMiddleCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeWheelsStopCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeZeroTiltCmd

class IntakeSubsystem(
        val leftServo: CRServo,
        val rightServo: CRServo,
        val armServo: Servo,
        val tiltServo: Servo
) : DeltaSubsystem() {

    init {
        reset()
    }

    override fun loop() {
    }

    fun reset() {
        + deltaSequence {
            - IntakeArmPositionMiddleCmd().dontBlock()
            - IntakeZeroTiltCmd().dontBlock()
            - IntakeWheelsStopCmd().dontBlock()
        }
    }

}