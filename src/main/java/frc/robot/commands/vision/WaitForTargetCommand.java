package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitForTargetCommand extends CommandBase {
  private VisionSubsystem VISION = RobotContainer.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public WaitForTargetCommand() {
    addRequirements(VISION);
  }

  @Override
  public void initialize() {
    logger.info("Waiting for target");
  }

  @Override
  public void execute() {
    logger.info("offset = {}, is valid = {}", VISION.getOffsetAngle(), VISION.isTargetValid());
  }

  @Override
  public boolean isFinished() {
    return (VISION.isTargetValid()
        && Math.abs(VISION.getOffsetAngle()) < Constants.VisionConstants.kCenteredRange);
  }

  @Override
  public void end(boolean interrupted) {
    logger.info("Target in range");
  }
}
