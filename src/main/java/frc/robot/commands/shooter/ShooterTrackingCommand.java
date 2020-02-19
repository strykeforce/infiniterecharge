package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShooterTrackingCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private static final VisionSubsystem VISION = RobotContainer.VISION;
  public Logger logger = LoggerFactory.getLogger("Track Command");

  private static double offset;
  private static double correction;

  public ShooterTrackingCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    VISION.setTrackingEnabled(true);
  }

  @Override
  public void execute() {
    offset = VISION.getOffsetAngle();
    correction = offset * .9;
    if (offset == 180) {
      if (SHOOTER.turretInRange()) {
        SHOOTER.seekTarget();
      }
    } else if (Math.abs(offset) > 1) {
      if (SHOOTER.turretInRange()) {
        logger.info("Offset = {}", offset);
        logger.info("Correction(90%) = {}", correction);
        SHOOTER.rotateTurret(-correction);
      }
    } else SHOOTER.rotateTurret(0);
  }

  @Override
  public boolean isFinished() {
    return !VISION.isTrackingEnabled();
  }
}
