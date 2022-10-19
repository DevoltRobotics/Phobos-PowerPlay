package org.firstinspires.ftc.phoboscode

import com.github.serivesmejia.deltasimple.SimpleHardware
import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.phoboscode.rr.drive.SampleMecanumDrive

class PhobosHardware : SimpleHardware() {

    val drive by lazy { SampleMecanumDrive(hardwareMap) }

    val turretMotor by hardware<DcMotorEx>("tr")

    val sliderLeftMotor by hardware<DcMotorEx>("sl")
    val sliderRightMotor by hardware<DcMotorEx>("sr")

    val sliderTouch by hardware<RevTouchSensor>("st")

}