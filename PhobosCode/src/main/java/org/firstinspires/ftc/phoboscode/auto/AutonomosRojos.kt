package org.firstinspires.ftc.phoboscode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA
import org.firstinspires.ftc.phoboscode.auto.AutonomoB

@Autonomous(name = "R - Izquierda Facundo", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoIzquierdaFull : AutonomoA(Alliance.RED)

@Autonomous(name = "R - Izquierda NO-CENTER Facundo", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoIzquierdaNoCenterFull : AutonomoA(Alliance.RED, centerline = false)

@Autonomous(name = "R - Derecha Leonardo", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoDerechaFull : AutonomoB(Alliance.RED)

@Autonomous(name = "R - Derecha NO-CENTER Leonardo", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoDerechaNoCenterFull : AutonomoB(Alliance.RED, centerline = false)