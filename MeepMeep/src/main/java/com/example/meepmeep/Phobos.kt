package com.example.meepmeep

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder
import com.noahbres.meepmeep.roadrunner.DriveShim
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequenceBuilder

enum class SleevePattern { A, B, C }

fun main() {
    val meepMeep = MeepMeep(600)

    // Declare our first bot
    val myFirstBot = DefaultBotBuilder(meepMeep) // We set this bot to be blue
            .setColorScheme(ColorSchemeBlueDark())
            .setConstraints(60.0, 60.0, Math.toRadians(180.0), Math.toRadians(180.0), 17.5)
            .followTrajectorySequence { drive ->
                drive.trajectorySequenceBuilder(Pose2d(35.0, -58.0, Math.toRadians(90.0))).apply {
                    lineToConstantHeading(Vector2d(35.5, 4.2)) // TODO: Preload cone score position
                    waitSeconds(0.5)

                    setReversed(true)
                    splineToConstantHeading(Vector2d(45.0, -8.6), Math.toRadians(0.0))
                    lineToConstantHeading(Vector2d(59.5, -8.6))
                    //splineTo(Vector2d(-50.0, -7.6), Math.toRadians(90.0))
                }.build()
            }

    // Declare out second bot
    val mySecondBot = DefaultBotBuilder(meepMeep) // We set this bot to be red
            .setColorScheme(ColorSchemeRedDark())
            .setConstraints(60.0, 60.0, Math.toRadians(180.0), Math.toRadians(180.0), 18.0)
            .followTrajectorySequence { drive ->
                sequence(drive, SleevePattern.A)
            }

    meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_KAI_DARK)
            .setDarkMode(true)
            .setBackgroundAlpha(0.95f) // Add both of our declared bot entities
            .addEntity(myFirstBot)
            //.addEntity(mySecondBot)
            .start()
}


val startPose = Pose2d(35.0, -58.0, Math.toRadians(90.0))
val cycles = 2

fun sequence(drive: DriveShim, sleevePattern: SleevePattern) = drive.trajectorySequenceBuilder(startPose).apply {
    lineToConstantHeading(Vector2d(35.0, 5.0))

    // put it
    lineToConstantHeading(Vector2d(34.0, 5.0))
    waitSeconds(1.2)

    waitSeconds(0.9)

    // just park here when we won`t be doing any cycles
    if(cycles == 0) {
        park(sleevePattern)

        return@apply
    }

    lineToSplineHeading(Pose2d(35.0, -9.2, Math.toRadians(0.0)))

    lineToLinearHeading(Pose2d(53.0, -9.2, Math.toRadians(0.0)))

    lineToConstantHeading(Vector2d(54.5, -9.2))
    waitSeconds(1.5)

    repeat(cycles - 1) {
        putOnHigh(400.0)

        lineToLinearHeading(Pose2d(53.0, -9.2, Math.toRadians(0.0)))
        lineToConstantHeading(Vector2d(54.5, -9.2))
        waitSeconds(1.0)
    }

    putOnHigh()

    when(sleevePattern) {
        SleevePattern.A -> {
            lineToLinearHeading(Pose2d(55.0, -12.0, Math.toRadians(0.0)))
        }
        SleevePattern.B -> {
            lineToLinearHeading(Pose2d(35.0, -12.0, Math.toRadians(0.0)))
        }
        SleevePattern.C -> {
            lineToLinearHeading(Pose2d(12.0, -12.0, Math.toRadians(270.0)))
        }
    }
}.build()

fun TrajectorySequenceBuilder.park(sleevePattern: SleevePattern) {
    when(sleevePattern) {
        SleevePattern.A -> {
            lineToSplineHeading(Pose2d(35.0, -33.0, java.lang.Math.toRadians(0.0)))

            setReversed(true)
            splineToConstantHeading(Vector2d(58.0, -35.0), java.lang.Math.toRadians(180.0))
            setReversed(false)
        }
        SleevePattern.B -> {
            lineToLinearHeading(Pose2d(35.0, -35.0, java.lang.Math.toRadians(0.0)))
        }
        SleevePattern.C -> {
            lineToSplineHeading(Pose2d(35.0, -33.0, java.lang.Math.toRadians(0.0)))
            lineToConstantHeading(Vector2d(11.5, -35.0))

            turn(Math.toRadians(-90.0))
        }
    }
}

fun TrajectorySequenceBuilder.putOnHigh(endingLiftPos: Double? = null) {
    lineToLinearHeading(Pose2d(22.5, -12.0, Math.toRadians(0.0)))

    waitSeconds(2.0)
}