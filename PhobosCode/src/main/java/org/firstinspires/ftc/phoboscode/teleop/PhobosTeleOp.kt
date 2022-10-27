package org.firstinspires.ftc.phoboscode.teleop

import com.github.serivesmejia.deltacommander.command.DeltaRunCmd
import com.github.serivesmejia.deltacommander.endRightAway
import com.github.serivesmejia.deltacommander.stopOn
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.phoboscode.PhobosOpMode
import org.firstinspires.ftc.phoboscode.command.intake.*
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToHighCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToLowCmd
import org.firstinspires.ftc.phoboscode.command.lift.LiftMoveToMidCmd
import org.firstinspires.ftc.phoboscode.command.mecanum.FieldCentricMecanumCmd
import org.firstinspires.ftc.phoboscode.command.mecanum.RobotCentricMecanumCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveCmd
import org.firstinspires.ftc.phoboscode.command.turret.TurretMoveToAngleCmd

@TeleOp(name = "TeleOp")
class PhobosTeleOp : PhobosOpMode() {

    override fun setup() {
        /* START A */

        // MECANUM
        + RobotCentricMecanumCmd(gamepad1)

        // INTAKE

        superGamepad1.scheduleOn(Button.A,
                IntakeWheelsAbsorbCmd(),
                IntakeWheelsStopCmd()
        )

        superGamepad1.scheduleOn(Button.B,
                IntakeWheelsReleaseCmd(),
                IntakeWheelsStopCmd()
        )

        /* START B */

        // LIFT

        liftSubsystem.defaultCommand = LiftMoveCmd { (-gamepad2.left_stick_y).toDouble() }

        // lift positions
        superGamepad2.scheduleOnPress(Button.Y,
                LiftMoveToHighCmd()
        ) { !liftSubsystem.isBusy }

        superGamepad2.scheduleOnPress(Button.B,
                LiftMoveToMidCmd()
        ) { !liftSubsystem.isBusy }

        superGamepad2.scheduleOnPress(Button.A,
                LiftMoveToLowCmd()
        ) { !liftSubsystem.isBusy }

        // INTAKE

        intakeArmSubsystem.defaultCommand = IntakeArmPositionIncrementCmd { (-gamepad2.right_stick_y).toDouble() * 0.003 }

        superGamepad2.toggleScheduleOn(Button.X,
                IntakeTiltCmd(0.3).endRightAway(),
                IntakeZeroTiltCmd().endRightAway()
        )

        // TURRET

        + TurretMoveCmd { (gamepad2.left_trigger - gamepad2.right_trigger).toDouble() * 0.8}

        // turret positions
        superGamepad2.scheduleOnPress(Button.DPAD_UP,
            TurretMoveToAngleCmd(0.0)
        )

        superGamepad2.scheduleOnPress(Button.DPAD_LEFT,
            TurretMoveToAngleCmd(90.0)
        )

        superGamepad2.scheduleOnPress(Button.DPAD_RIGHT,
            TurretMoveToAngleCmd(-90.0)
        )

        // telemetry
        + DeltaRunCmd {
            telemetry.addData("turret pos", hardware.turretMotor.currentPosition)
            telemetry.addData("turret target", turretSubsystem.controller.targetPosition)

            telemetry.addData("lift pos", hardware.sliderLeftMotor.currentPosition)
            telemetry.addData("lift target", liftSubsystem.controller.targetPosition)

            telemetry.addData("lift power", liftSubsystem.power)
            telemetry.addData("lift top pressed", hardware.sliderTopLimitSensor.isPressed)
            telemetry.addData("lift bottom pressed", hardware.sliderBottomLimitSensor.isPressed)

            telemetry.update()
        }
    }

}