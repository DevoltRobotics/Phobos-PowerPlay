package org.firstinspires.ftc.phoboscode.subsystem

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.github.serivesmejia.deltacommander.endRightAway
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmAndTiltZeroCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionMiddleCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeWheelsStopCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeZeroTiltCmd

class IntakeArmSubsystem(
        val armServo: Servo,
        val tiltServo: Servo
) : DeltaSubsystem() {

    var tiltSaveThreshold = 0.7

    var downTilt = 0.5

    private var isTiltedForLimit = false

    init {
        armServo.direction = Servo.Direction.REVERSE

        defaultCommand = IntakeArmAndTiltZeroCmd()
    }

    override fun loop() {
       if(armServo.position >= tiltSaveThreshold) {
            tiltServo.position = 0.9
            isTiltedForLimit = true
        } else if(isTiltedForLimit) {
            tiltServo.position = downTilt
            isTiltedForLimit = false
        }

        //armServo.position = Range.clip(armServo.position, 0.0, 0.8)
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