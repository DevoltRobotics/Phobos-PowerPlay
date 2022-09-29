package org.firstinspires.ftc.phoboscode.teleop

import com.github.serivesmejia.deltacommander.command.DeltaRunCmd
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.phoboscode.PhobosOpMode
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd
import org.firstinspires.ftc.phoboscode.command.mecanum.FieldCentricMecanumCmd
import org.firstinspires.ftc.phoboscode.command.mecanum.RobotCentricMecanumCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd

@TeleOp(name = "TeleOp")
class PhobosTeleOp : PhobosOpMode() {

    override fun setup() {
        // START A
        + RobotCentricMecanumCmd(gamepad1)

        // START B
        + LiftMoveCmd { (-gamepad2.left_stick_y).toDouble() }

        + TurretMoveCmd { (gamepad2.left_trigger - gamepad2.right_trigger).toDouble() * 0.8}

        superGamepad2.scheduleOnPress(Button.X, DeltaRunCmd {
            turretSubsystem.reset()
        })

        // turret positions
        superGamepad2.scheduleOnPress(Button.DPAD_UP,
            TurretMoveToAngleCmd(0.0))

        superGamepad2.scheduleOnPress(Button.DPAD_LEFT,
            TurretMoveToAngleCmd(90.0))

        superGamepad2.scheduleOnPress(Button.DPAD_RIGHT,
            TurretMoveToAngleCmd(-90.0))

        // telemetry
        + DeltaRunCmd {
            telemetry.addData("turret pos", hardware.turretMotor.currentPosition)
            telemetry.addData("turret target", turretSubsystem.controller.targetPosition)
            telemetry.update()
        }
    }

}