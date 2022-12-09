package org.firstinspires.ftc.phoboscode.command.turret

import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.phoboscode.vision.ConeTrackingPipeline

class TurretConeTrackingCmd(
    val pipeline: ConeTrackingPipeline
) : TurretMoveToAngleCmd(0.0) {

    override fun run() {
        angle = Range.clip(-pipeline.lastTurretAngle, -45.0, 45.0)

        super.run()
    }

}