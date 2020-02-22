package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.VisionSubsystem;

public class StopVisionTrackingCommand extends InstantCommand {
  private static final VisionSubsystem vision = RobotContainer.VISION;

  @Override
  public void initialize() {
    vision.setTrackingEnabled(false);
  }
}
