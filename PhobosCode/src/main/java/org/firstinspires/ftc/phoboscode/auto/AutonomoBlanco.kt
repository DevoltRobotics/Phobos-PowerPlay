package org.firstinspires.ftc.phoboscode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.Side
import org.firstinspires.ftc.phoboscode.rr.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.phoboscode.vision.SleevePattern

@Autonomous(name = "Blanco", group = "1")
class AutonomoBlanco : AutonomoBase(Alliance.RED, Side.LEFT) {

    override fun sequence(sleevePattern: SleevePattern): TrajectorySequence {
        TODO("Not yet implemented")
    }

}