package org.firstinspires.ftc.phoboscode.subsystem

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.github.serivesmejia.deltacommander.endRightAway
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionMiddleCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeWheelsStopCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeZeroTiltCmd

class IntakeArmSubsystem(
        val armServo: Servo,
        val tiltServo: Servo
) : DeltaSubsystem() {

    init {
        reset()

        armServo.direction = Servo.Direction.REVERSE
    }

    override fun loop() {
    }

    fun reset() {
        + deltaSequence {
            - IntakeArmPositionMiddleCmd().endRightAway()
            - IntakeZeroTiltCmd().endRightAway()
        }
    }

}

class IntakeWheelsSubsystem(
    val leftServo: CRServo,
    val rightServo: CRServo
) : DeltaSubsystem() {

    init {
        defaultCommand = IntakeWheelsStopCmd()

        leftServo.direction = DcMotorSimple.Direction.REVERSE
        rightServo.direction = DcMotorSimple.Direction.REVERSE
    }

    override fun loop() {
    }

}