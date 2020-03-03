package frc.robot.commands.hood;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HoodAimCommand extends CommandBase {
  private static final HoodSubsystem HOOD = RobotContainer.HOOD;
  private static final VisionSubsystem VISION = RobotContainer.VISION;

  public Logger logger = LoggerFactory.getLogger("Hood Track Command");

  public HoodAimCommand() {
    addRequirements(HOOD);
  }

  @Override
  public void initialize() {
    VISION.setTrackingEnabled(true);
    if (VISION.isTargetValid()) {
      HOOD.setHoodPosition(
          (int) VISION.getHoodSetpoint(VISION.getBestTableIndex())
              + VISION.getHoodTicksAdjustment());
    }
  }

  @Override
  public boolean isFinished() {
    return HOOD.hoodAtTarget();
  }
}
