package org.firstinspires.ftc.deimoscode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.deimoscode.rr.drive.SampleMecanumDrive;

@TeleOp(name = "TeleOp")
public class DeimosTeleOp extends LinearOpMode {

    SampleMecanumDrive drive;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new SampleMecanumDrive(hardwareMap);

        waitForStart();

        while(opModeIsActive()) {
            drive.setWeightedDrivePower(new Pose2d(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x));
        }

    }

}
