package org.firstinspires.ftc.phoboscode.subsystem

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import org.firstinspires.ftc.phoboscode.rr.drive.SampleMecanumDrive

class MecanumSubsystem(val drive: SampleMecanumDrive) : DeltaSubsystem() {

    override fun loop() {
        drive.update()
    }

}