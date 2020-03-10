package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.TurretSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActuateTuningCommand extends CommandBase {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private HoodSubsystem HOOD = RobotContainer.HOOD;
  private TurretSubsystem TURRET = RobotContainer.TURRET;
  public Logger logger = LoggerFactory.getLogger("Execute Tuning Command");

  private int turretSetpoint;
  private int hoodSetpoint;
  private int shooterSpeed;

  public ActuateTuningCommand() {
    addRequirements(SHOOTER, HOOD, TURRET);
  }

  @Override
  public void initialize() {
    logger.info("Actuating tuning setpoints");

    turretSetpoint = (int) SmartDashboard.getNumber("Tuning/turretPos", 0);
    hoodSetpoint = (int) SmartDashboard.getNumber("Tuning/hoodPos", 0);
    shooterSpeed = (int) SmartDashboard.getNumber("Tuning/shooterVel", 0);
    SHOOTER.run(shooterSpeed);
    TURRET.setTurret(turretSetpoint);
    logger.info("Rotating Turret to {} ticks", turretSetpoint);
    HOOD.setHoodPosition(hoodSetpoint);
  }

  @Override
  public boolean isFinished() {
    return TURRET.turretAtTarget() && HOOD.hoodAtTarget() && SHOOTER.atTargetSpeed();
  }

  @Override
  public void end(boolean interrupted) {
    logger.info("Tuning setpoints reached");
  }
}
