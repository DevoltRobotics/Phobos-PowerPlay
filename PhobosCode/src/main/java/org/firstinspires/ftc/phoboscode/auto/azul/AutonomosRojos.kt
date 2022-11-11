package org.firstinspires.ftc.phoboscode.auto.azul

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA
import org.firstinspires.ftc.phoboscode.auto.AutonomoB

@Autonomous(name = "R - Izquierda Roxana Park", group = "#RFINAL")
class AutonomoRojoIzquierdaFull : AutonomoB(Alliance.RED)

@Autonomous(name = "R - Izquierda Uno Park", group = "#RFINAL")
class AutonomoRojoIzquierdaUno : AutonomoB(Alliance.RED, cycles = 0)

@Autonomous(name = "R - Izquierda Roxana Park", group = "#RFINAL")
class AutonomoRojoDerechaFull : AutonomoA(Alliance.RED)

@Autonomous(name = "R - Izquierda Uno Park", group = "#RFINAL")
class AutonomoRojoDerechaUno : AutonomoA(Alliance.RED, cycles = 0)