package org.firstinspires.ftc.phoboscode.auto.azul

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA
import org.firstinspires.ftc.phoboscode.auto.AutonomoB

@Autonomous(name = "R - Izquierda Facundo", group = "#RFINAL")
class AutonomoRojoIzquierdaFull : AutonomoA(Alliance.RED)

@Autonomous(name = "R - Izquierda Uno Park", group = "#RFINAL")
class AutonomoRojoIzquierdaUno : AutonomoA(Alliance.RED, cycles = 0)

@Autonomous(name = "R - Derecha Leonardo", group = "#RFINAL")
class AutonomoRojoDerechaFull : AutonomoB(Alliance.RED)

@Autonomous(name = "R - Derecha Uno Park", group = "#RFINAL")
class AutonomoRojoDerechaUno : AutonomoB(Alliance.RED, cycles = 0)