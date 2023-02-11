package org.firstinspires.ftc.phoboscode

import com.github.serivesmejia.deltasimple.SimpleHardware
import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.hardware.rev.RevTouchSensor

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.phoboscode.hardware.MBUltraSonic
import org.firstinspires.ftc.phoboscode.hardware.UltraSonicRelocalizer
import org.firstinspires.ftc.phoboscode.rr.drive.SampleMecanumDrive

class PhobosHardware : SimpleHardware() {

    val drive by lazy { SampleMecanumDrive(hardwareMap) }

    val leftUltrasonic by hardware<AnalogInput>("lu")
    val leftMBUltraSonic by lazy { MBUltraSonic(leftUltrasonic, (13.25 / 0.185)) }

    val rightUltrasonic by hardware<AnalogInput>("ru")
    val rightMBUltraSonic by lazy { MBUltraSonic(rightUltrasonic, (28.5 / 0.405)) }

    val intakeLeftServo by hardware<CRServo>("il")
    val intakeRightServo by hardware<CRServo>("ir")

    val intakeArmServo by hardware<Servo>("ia")
    val intakeTiltServo by hardware<Servo>("it")

    val turretMotor by hardware<DcMotorEx>("tr")

    val sliderLeftMotor by hardware<DcMotorEx>("sl")
    val sliderRightMotor by hardware<DcMotorEx>("sr")

    val sliderTopLimitSensor by hardware<RevColorSensorV3>("st")
    val sliderBottomLimitSensor by hardware<RevTouchSensor>("sb")

    val odometryRetractServo by hardware<Servo>("or")

    override fun init() {
        // squeezing out extra degrees from gobilda servos
        (intakeArmServo as ServoImplEx).pwmRange = PwmControl.PwmRange(500.0, 2500.0)

        (intakeLeftServo as CRServoImplEx).pwmRange = PwmControl.PwmRange(500.0, 2500.0)
        (intakeRightServo as CRServoImplEx).pwmRange = PwmControl.PwmRange(500.0, 2500.0)

        (odometryRetractServo as ServoImplEx).pwmRange = PwmControl.PwmRange(500.0, 2500.0)
    }

}