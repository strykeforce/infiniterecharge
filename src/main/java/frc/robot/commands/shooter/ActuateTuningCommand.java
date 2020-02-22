package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActuateTuningCommand extends CommandBase {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  public Logger logger = LoggerFactory.getLogger("Execute Tuning Command");

  private int turretSetpoint;
  private int hoodSetpoint;
  private int shooterSpeed;

  public ActuateTuningCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    logger.info("Actuating tuning setpoints");

    turretSetpoint = (int) SmartDashboard.getNumber("Tuning/turretPos", 0);
    hoodSetpoint = (int) SmartDashboard.getNumber("Tuning/hoodPos", 0);
    shooterSpeed = (int) SmartDashboard.getNumber("Tuning/shooterVel", 0);
    SHOOTER.applyTuningSetpoints(turretSetpoint, hoodSetpoint, shooterSpeed);
  }

  @Override
  public boolean isFinished() {
    return SHOOTER.turretAtTarget() && SHOOTER.hoodAtTarget() && SHOOTER.atTargetSpeed();
  }

  @Override
  public void end(boolean interrupted) {
    logger.info("Tuning setpoints reached");
  }
}
