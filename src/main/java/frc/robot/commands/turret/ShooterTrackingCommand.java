package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShooterTrackingCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;
  private static final VisionSubsystem VISION = RobotContainer.VISION;
  public Logger logger = LoggerFactory.getLogger("Track Command");

  public ShooterTrackingCommand() {
    addRequirements(TURRET);
  }

  @Override
  public void initialize() {
    VISION.setTrackingEnabled(true);
  }

  @Override
  public void execute() {
    TURRET.seekTarget();
  }

  @Override
  public boolean isFinished() {
    return !VISION.isTrackingEnabled();
  }
}
