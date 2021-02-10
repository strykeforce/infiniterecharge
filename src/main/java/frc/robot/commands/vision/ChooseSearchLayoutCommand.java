package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DeadeyeA1;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChooseSearchLayoutCommand extends InstantCommand {
  private static final VisionSubsystem VISION = RobotContainer.VISION;
  public Logger logger = LoggerFactory.getLogger("Layout Select Command");

  public ChooseSearchLayoutCommand() {}

  @Override
  public void initialize() {
    DeadeyeA1.Layout layout = DeadeyeA1.Layout.INVALID;
    int attempts = 0;
    while (layout == DeadeyeA1.Layout.INVALID && attempts < 3) {
      layout = VISION.getLayout();
      switch (layout) {
        case RED1:
          logger.info("Layout: Red-1");
          break;
        case RED2:
          logger.info("Layout: Red-2");
          break;
        case BLUE1:
          logger.info("Layout: Blue-1");
          break;
        case BLUE2:
          logger.info("Layout: Blue-2");
          break;
        case INVALID:
          logger.info("Layout: Invalid, retrying");
      }
      attempts++;
    }
  }
}
