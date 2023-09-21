package org.firstinspires.ftc.phoboscode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.phoboscode.Alliance

@Autonomous(name = "R - Izquierda", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoIzquierdaFull : AutonomoA(Alliance.RED)

@Autonomous(name = "R - Izquierda A-NO-CENTER", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoIzquierdaANoCenterFull : AutonomoA(Alliance.RED, centerline = false)

@Autonomous(name = "R - Izquierda PRUEBA-NO-CENTER", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoIzquierdaBNoCenterFull : AutonomoAPrueba(Alliance.RED, centerline = false)

@Autonomous(name = "R - Derecha", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoDerechaFull : AutonomoB(Alliance.RED)

@Autonomous(name = "R - Derecha NO-CENTER", group = "#RFINAL", preselectTeleOp = "Nacho Libre")
class AutonomoRojoDerechaNoCenterFull : AutonomoB(Alliance.RED, centerline = false)