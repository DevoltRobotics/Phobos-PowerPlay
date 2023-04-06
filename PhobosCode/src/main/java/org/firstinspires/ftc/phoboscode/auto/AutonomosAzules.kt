package org.firstinspires.ftc.phoboscode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA
import org.firstinspires.ftc.phoboscode.auto.AutonomoB

@Autonomous(name = "A - Izquierda Roxana", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulIzquierdaFull : AutonomoA(Alliance.BLUE)

@Autonomous(name = "A - Izquierda NO-CENTER Roxana", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulIzquierdaNoCenterFull : AutonomoA(Alliance.BLUE, centerline = false)

@Autonomous(name = "A - Derecha Susanita", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulDerechaFull : AutonomoB(Alliance.BLUE)

@Autonomous(name = "A - Derecha NO-CENTER Susanita", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulDerechaNoCenterFull : AutonomoB(Alliance.BLUE, centerline = false)