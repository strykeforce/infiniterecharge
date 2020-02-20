package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteTuningCommand extends CommandBase {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  public Logger logger = LoggerFactory.getLogger("Save Tuning Command");

  public ExecuteTuningCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    logger.info("Actuating tuning setpoints");
    SHOOTER.applyTuningSetpoints();
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
