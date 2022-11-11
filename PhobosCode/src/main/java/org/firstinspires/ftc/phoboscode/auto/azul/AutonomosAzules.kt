package org.firstinspires.ftc.phoboscode.auto.azul

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA

@Autonomous(name = "A - Izquierda Roxana Park", group = "#AFINAL")
class AutonomoAzulIzquierdaFull : AutonomoA(Alliance.BLUE)

@Autonomous(name = "A - Izquierda Uno Park", group = "#AFINAL")
class AutonomoAzulIzquierdaUno : AutonomoA(Alliance.BLUE, cycles = 0)

@Autonomous(name = "A - Derecha Susanita Park", group = "#AFINAL")
class AutonomoAzulDerechaFull : AutonomoA(Alliance.BLUE)

@Autonomous(name = "A - Derecha Uno Park", group = "#AFINAL")
class AutonomoAzulDerechaUno : AutonomoA(Alliance.BLUE, cycles = 0)