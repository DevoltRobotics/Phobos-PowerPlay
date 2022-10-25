package com.example.meepmeep

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder

fun main() {
    val meepMeep = MeepMeep(600)

    // Declare our first bot
    val myFirstBot = DefaultBotBuilder(meepMeep) // We set this bot to be blue
            .setColorScheme(ColorSchemeBlueDark())
            .setConstraints(60.0, 60.0, Math.toRadians(180.0), Math.toRadians(180.0), 17.5)
            .followTrajectorySequence { drive ->
                drive.trajectorySequenceBuilder(Pose2d(-35.0, -58.0, Math.toRadians(90.0))).apply {
                    lineToConstantHeading(Vector2d(-35.0, -23.0))
                    waitSeconds(1.0)
                    splineToSplineHeading(Pose2d(-52.0, -12.5, Math.toRadians(180.0)), Math.toRadians(178.0))

                    waitSeconds(1.2)

                    repeat(3) {
                        lineToConstantHeading(Vector2d(-24.0, -11.5))
                        waitSeconds(1.2)
                        lineToConstantHeading(Vector2d(-54.0, -12.5))
                        waitSeconds(1.2)
                    }

                    lineToConstantHeading(Vector2d(-24.0, -11.5))
                    waitSeconds(1.2)

                    val park = 1

                    lineTo(Vector2d(-57.0, -11.5))

                    //lineToSplineHeading(Pose2d(-35.0, -11.5, Math.toRadians(270.0)))

                    //lineToSplineHeading(Pose2d(-12.0, -11.5, Math.toRadians(270.0)))
                }.build()
            }

    // Declare out second bot
    val mySecondBot = DefaultBotBuilder(meepMeep) // We set this bot to be red
            .setColorScheme(ColorSchemeRedDark())
            .setConstraints(60.0, 60.0, Math.toRadians(180.0), Math.toRadians(180.0), 18.0)
            .followTrajectorySequence { drive ->
                drive.trajectorySequenceBuilder(Pose2d(30.0, 30.0, Math.toRadians(180.0)))
                        .forward(30.0)
                        .turn(Math.toRadians(90.0))
                        .forward(30.0)
                        .turn(Math.toRadians(90.0))
                        .forward(30.0)
                        .turn(Math.toRadians(90.0))
                        .forward(30.0)
                        .turn(Math.toRadians(90.0))
                        .build()
            }

    meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_KAI_DARK)
            .setDarkMode(true)
            .setBackgroundAlpha(0.95f) // Add both of our declared bot entities
            .addEntity(myFirstBot)
            .addEntity(mySecondBot)
            .start()
}