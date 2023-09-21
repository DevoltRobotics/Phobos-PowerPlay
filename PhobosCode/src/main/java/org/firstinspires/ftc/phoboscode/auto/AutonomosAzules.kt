package org.firstinspires.ftc.phoboscode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance

@Autonomous(name = "A - Izquierda", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulIzquierdaFull : AutonomoA(Alliance.BLUE)

@Autonomous(name = "A - Izquierda A-NO-CENTER", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulIzquierdaANoCenterFull : AutonomoA(Alliance.BLUE, centerline = false)

@Autonomous(name = "A - Izquierda PRUEBA-NO-CENTER", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulIzquierdaBNoCenterFull : AutonomoAPrueba(Alliance.BLUE, centerline = false)

@Autonomous(name = "A - Derecha", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulDerechaFull : AutonomoB(Alliance.BLUE)

@Autonomous(name = "A - Derecha NO-CENTER Susanita", group = "#AFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoAzulDerechaNoCenterFull : AutonomoB(Alliance.BLUE, centerline = false)