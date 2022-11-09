package org.firstinspires.ftc.phoboscode.auto.azul

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance
import org.firstinspires.ftc.phoboscode.auto.AutonomoA

@Autonomous(name = "A - Izquierda Roxana Park", group = "#FINAL")
class AutonomoAzulIzquierdaFull : AutonomoA(Alliance.BLUE)

@Autonomous(name = "A - Izquierda Uno Park", group = "#FINAL")
class AutonomoAzulIzquierdaUno : AutonomoA(Alliance.BLUE, cycles = 0)