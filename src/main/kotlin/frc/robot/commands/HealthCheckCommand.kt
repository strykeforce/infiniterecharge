package frc.robot.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import frc.robot.RobotContainer
import mu.KotlinLogging
import org.strykeforce.thirdcoast.healthcheck.HealthCheck
import org.strykeforce.thirdcoast.healthcheck.healthCheck

private val logger = KotlinLogging.logger { }

class HealthCheckCommand : CommandBase() {
    init {
        addRequirements(
            RobotContainer.DRIVE,
            RobotContainer.INTAKE,
            RobotContainer.MAGAZINE,
            RobotContainer.SHOOTER,
            RobotContainer.HOOD,
            RobotContainer.TURRET,
            RobotContainer.CLIMBER
        )
    }

    private lateinit var healthCheck: HealthCheck

    override fun initialize() {
        healthCheck = healthCheck {
            //Azimuth Motors
            talonCheck {
                name = "swerve azimuth tests"
                talons = RobotContainer.DRIVE.allWheels.map { it.azimuthTalon }

                val volt3supplyCurrentRange = 0.25..0.75
                val volt6supplyCurrentRange = 0.5..1.25
                val volt9supplyCurrentRange = 1.0..1.5

                val volt3statorCurrentRange = 0.25..0.75
                val volt6statorCurrentRange = 0.5..1.25
                val volt9statorCurrentRange = 1.0..1.5

                timedTest {
                    percentOutput = 0.25
                    supplyCurrentRange = volt3supplyCurrentRange
                    statorCurrentRange = volt3statorCurrentRange
                    speedRange = 215..250
                }

                timedTest {
                    percentOutput = -0.25
                    supplyCurrentRange = volt3supplyCurrentRange
                    statorCurrentRange = volt3statorCurrentRange
                    speedRange = -250..-215
                }

                timedTest {
                    percentOutput = 0.5
                    supplyCurrentRange = volt6supplyCurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = 475..535
                }

                timedTest {
                    percentOutput = -0.5
                    supplyCurrentRange = volt6supplyCurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = -535..-475
                }

                timedTest {
                    percentOutput = 0.75
                    supplyCurrentRange = volt9supplyCurrentRange
                    statorCurrentRange = volt9statorCurrentRange
                    speedRange = 750..820
                }

                timedTest {
                    percentOutput = -0.75
                    supplyCurrentRange = volt9supplyCurrentRange
                    statorCurrentRange = volt9statorCurrentRange
                    speedRange = -820..-750
                }
            }

            //Drive Motors
            talonCheck {
                name = "swerve drive tests"
                talons = RobotContainer.DRIVE.allWheels.map { it.driveTalon }

                val volt3supplyCurrentRange = 0.5..1.125
                val volt6supplyCurrentRange = 1.0..2.0
                val volt12supplyCurrentRange = 2.5..5.0

                val volt3statorCurrentRange = 0.5..1.125
                val volt6statorCurrentRange = 1.0..2.0
                val volt12statorCurrentRange = 2.5..5.0

                timedTest {
                    percentOutput = 0.25
                    supplyCurrentRange = volt3supplyCurrentRange
                    statorCurrentRange = volt3statorCurrentRange
                    speedRange = 8500..9500
                }

                timedTest {
                    percentOutput = -0.25
                    supplyCurrentRange = volt3supplyCurrentRange
                    statorCurrentRange = volt3statorCurrentRange
                    speedRange = -9500..-8500
                }

                timedTest {
                    percentOutput = 0.5
                    supplyCurrentRange = volt6supplyCurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = 17200..19500
                }

                timedTest {
                    percentOutput = -0.5
                    supplyCurrentRange = volt6supplyCurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = -19500..-17200
                }

                timedTest {
                    percentOutput = 1.0
                    supplyCurrentRange = volt12supplyCurrentRange
                    statorCurrentRange = volt12statorCurrentRange
                    speedRange = 34750..39500
                }

                timedTest {
                    percentOutput = -1.0
                    supplyCurrentRange = volt12supplyCurrentRange
                    statorCurrentRange = volt12statorCurrentRange
                    speedRange = -39500..-34750
                }
            }

            //Intake Tests
            talonCheck {
                name = "intake tests"
                talons = RobotContainer.INTAKE.talons

                val volt6supplycurrentRange = 1.0..4.0

                val volt6statorCurrentRange = 1.0..4.0

                timedTest {
                    percentOutput = 0.5
                    supplyCurrentRange = volt6supplycurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = 3500..4500
                }

                timedTest {
                    percentOutput = -0.5
                    supplyCurrentRange = volt6supplycurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = -4500..-3500
                }
            }

            //Magazine Tests
            talonCheck {
                name = "magazine tests"
                talons = RobotContainer.MAGAZINE.talons

                val volt6supplyCurrentRange = 1.0..4.0
                val volt12supplyCurrentRange = 13.0..17.0

                val volt6statorCurrentRange = 1.0..4.0
                val volt12statorCurrentRange = 13.0..17.0

                timedTest {
                    percentOutput = 0.5
                    supplyCurrentRange = volt6supplyCurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = 2000..3000
                }

                timedTest {
                    percentOutput = -0.5
                    supplyCurrentRange = volt6supplyCurrentRange
                    statorCurrentRange = volt6statorCurrentRange
                    speedRange = -3000..-2000
                }

                timedTest {
                    percentOutput = 1.0
                    supplyCurrentRange = volt12supplyCurrentRange
                    statorCurrentRange = volt12statorCurrentRange
                    speedRange = 3500..5000
                }

                timedTest {
                    percentOutput = -1.0
                    supplyCurrentRange = volt12supplyCurrentRange
                    statorCurrentRange = volt12statorCurrentRange
                    speedRange = -5000..-3500
                }
            }

            //Shooter Tests
            talonCheck {
                name = "shooter tests"
                talons = RobotContainer.SHOOTER.talons

                val volt10_000supplyCurrentRange = 1.0..4.0
                val volt16_000supplyCurrentRange = 13.0..17.0

                val volt10_000statorCurrentRange = 1.0..4.0
                val volt16_000statorCurrentRange = 13.0..17.0

                followerTimedTest {
                    percentOutput = 0.53
                    supplyCurrentRange = volt10_000supplyCurrentRange
                    statorCurrentRange = volt10_000statorCurrentRange
                    speedRange = 8_000..12_000
                }

                followerTimedTest {
                    percentOutput = 0.84
                    supplyCurrentRange = volt16_000supplyCurrentRange
                    statorCurrentRange = volt16_000statorCurrentRange
                    speedRange = 14_000..19_000
                }

            }

            //Turret Tests
            talonCheck {
                name = "turret tests"
                talons = RobotContainer.TURRET.talons

                val supplyCurrent = 0.375..1.0
                val statorCurrent = 0.375..1.0


                positionTalon {
                    encoderTarget = 0
                    encoderGoodEnough = 100
                }

                positionTest {
                    percentOutput = 0.2
                    encoderChangeTarget = 25_000
                    encoderGoodEnough = 500
                    encoderTimeCount = 500

                    supplyCurrentRange = supplyCurrent
                    statorCurrentRange = statorCurrent
                    speedRange = 550..750
                }

                positionTest {
                    percentOutput = -0.2
                    encoderChangeTarget = 25_000
                    encoderGoodEnough = 500
                    encoderTimeCount = 500

                    supplyCurrentRange = supplyCurrent
                    statorCurrentRange = statorCurrent
                    speedRange = -750..-550
                }

            }

            //Hood Tests
            talonCheck {
                name = "hood tests"
                talons = RobotContainer.HOOD.talons

                val supplyCurrent = 0.375..1.0
                val statorCurrent = 0.375..1.0

                positionTalon {
                    encoderTarget = 0
                    encoderGoodEnough = 100
                }

                positionTest {
                    percentOutput = 0.2
                    encoderChangeTarget = 7000
                    encoderGoodEnough = 500
                    encoderTimeCount = 500

                    supplyCurrentRange = supplyCurrent
                    statorCurrentRange = statorCurrent
                }

                positionTest {
                    percentOutput = -0.2
                    encoderChangeTarget = 7000
                    encoderGoodEnough = 500
                    encoderTimeCount = 500

                    supplyCurrentRange = supplyCurrent
                    statorCurrentRange = statorCurrent
                }
            }

        }
    }

    override fun execute() {
        healthCheck.execute()
    }

    override fun isFinished() = healthCheck.isFinished()

    override fun end(interrupted: Boolean) {
        healthCheck.report()
        RobotContainer.DRIVE.zeroSwerve()
    }
}