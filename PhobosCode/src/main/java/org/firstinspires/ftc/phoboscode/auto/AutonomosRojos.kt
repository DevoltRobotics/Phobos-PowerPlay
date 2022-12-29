package org.firstinspires.ftc.phoboscode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA
import org.firstinspires.ftc.phoboscode.auto.AutonomoB

@Autonomous(name = "R - Izquierda Facundo", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoIzquierdaFull : AutonomoA(Alliance.RED)

@Autonomous(name = "R - Izquierda Uno Park", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoIzquierdaUno : AutonomoA(Alliance.RED, cycles = 0)

@Autonomous(name = "R - Derecha Leonardo", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoDerechaFull : AutonomoB(Alliance.RED)

@Autonomous(name = "R - Derecha Uno Park", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoDerechaUno : AutonomoB(Alliance.RED, cycles = 0)