package org.firstinspires.ftc.phoboscode.subsystem

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.github.serivesmejia.deltacommander.endRightAway
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmAndTiltZeroCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionMiddleCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeWheelsStopCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeZeroTiltCmd

class IntakeArmSubsystem(
        val armServo: Servo,
        val tiltServo: Servo
) : DeltaSubsystem() {

    init {
        armServo.direction = Servo.Direction.REVERSE

        defaultCommand = IntakeArmAndTiltZeroCmd()
    }

    override fun loop() {
        if(armServo.position >= 0.7) {
            tiltServo.position = 0.9
        } else if(tiltServo.position == 0.9) {
            tiltServo.position = 0.5
        }
    }

    fun reset() {
        armServo.position = 0.5
        tiltServo.position = 0.5
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