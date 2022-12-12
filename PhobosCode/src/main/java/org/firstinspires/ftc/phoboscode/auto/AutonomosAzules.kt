package org.firstinspires.ftc.phoboscode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA
import org.firstinspires.ftc.phoboscode.auto.AutonomoB

@Autonomous(name = "A - Izquierda Roxana", group = "#AFINAL")
class AutonomoAzulIzquierdaFull : AutonomoA(Alliance.BLUE)

@Autonomous(name = "A - Izquierda Uno Park", group = "#AFINAL")
class AutonomoAzulIzquierdaUno : AutonomoA(Alliance.BLUE, cycles = 0)

@Autonomous(name = "A - Derecha Susanita", group = "#AFINAL")
class AutonomoAzulDerechaFull : AutonomoB(Alliance.BLUE)

@Autonomous(name = "A - Derecha Uno Park", group = "#AFINAL")
class AutonomoAzulDerechaUno : AutonomoB(Alliance.BLUE, cycles = 0)