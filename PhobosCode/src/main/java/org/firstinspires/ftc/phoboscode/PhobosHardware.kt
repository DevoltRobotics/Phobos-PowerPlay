package org.firstinspires.ftc.phoboscode

import com.github.serivesmejia.deltasimple.SimpleHardware
import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.hardware.rev.RevTouchSensor

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.phoboscode.rr.drive.SampleMecanumDrive

class PhobosHardware : SimpleHardware() {

    val drive by lazy { SampleMecanumDrive(hardwareMap) }

    val intakeLeftServo by hardware<CRServo>("il")
    val intakeRightServo by hardware<CRServo>("ir")

    val intakeArmServo by hardware<Servo>("ia")
    val intakeTiltServo by hardware<Servo>("it")

    val intakePoleServo by hardware<Servo>("ip")

    val intakeUltrasonic by hardware<AnalogInput>("iu")

    val turretMotor by hardware<DcMotorEx>("tr")

    val sliderLeftMotor by hardware<DcMotorEx>("sl")
    val sliderRightMotor by hardware<DcMotorEx>("sr")

    val sliderTopLimitSensor by hardware<RevColorSensorV3>("st")
    val sliderBottomLimitSensor by hardware<RevTouchSensor>("sb")

    val odometryRetractServo by hardware<Servo>("or")

    override fun init() {
        intakeArmServo.gobilda()
        intakePoleServo.gobilda()

        intakeLeftServo.gobilda()
        intakeRightServo.gobilda()

        odometryRetractServo.gobilda()
    }

    // squeezing out extra degrees from gobilda servos
    fun PwmControl.gobilda() {
        pwmRange = PwmControl.PwmRange(500.0, 2500.0)
    }

    fun Servo.gobilda() = (this as PwmControl).gobilda()
    fun CRServo.gobilda() = (this as PwmControl).gobilda()

}