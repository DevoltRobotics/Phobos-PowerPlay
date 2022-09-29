package com.github.serivesmejia.deltadrive.utils

import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl
import org.firstinspires.ftc.robotcore.internal.system.AppUtil

object Robot {

    fun getOpModeManager() = OpModeManagerImpl.getOpModeManagerOfActivity(AppUtil.getInstance().rootActivity)

    fun getCurrentOpMode() = getOpModeManager().activeOpMode

    fun getCurrentOpModeName() = getOpModeManager().activeOpModeName

    fun getHardwareMap() = getOpModeManager().hardwareMap

}