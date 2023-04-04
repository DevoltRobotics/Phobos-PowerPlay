package org.firstinspires.ftc.phoboscode.auto

import com.github.serivesmejia.deltacommander.command.DeltaInstantCmd
import com.github.serivesmejia.deltacommander.dsl.deltaSequence
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.PhobosOpMode
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionMiddleCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeArmPositionSaveCmd
import org.firstinspires.ftc.phoboscode.command.intake.IntakeTiltCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToPosCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd

@Autonomous(name = "Jueces", group = "#B")
class Jueces : PhobosOpMode() {

    override fun setup() {
        turretSubsystem.reset()

        + deltaSequence {
            - IntakeArmPositionSaveCmd().dontBlock()
            - TurretMoveToAngleCmd(-90.0).dontBlock()

            - waitForSeconds(1.0)

            - LiftMoveToPosCmd(800.0).dontBlock()

            - waitForSeconds(0.5)

            - IntakeArmPositionCmd(0.6).dontBlock()

            repeat(3) {
                - IntakeTiltCmd(0.7).dontBlock()
                - waitForSeconds(0.2)

                - IntakeTiltCmd(0.5).dontBlock()
                - waitForSeconds(0.3)
            }

            - waitForSeconds(0.4)

            - IntakeArmPositionSaveCmd().dontBlock()
            - LiftMoveToPosCmd(0.0).dontBlock()

            - waitForSeconds(1.0)

            - DeltaInstantCmd {
                requestOpModeStop()
            }
        }
    }

}