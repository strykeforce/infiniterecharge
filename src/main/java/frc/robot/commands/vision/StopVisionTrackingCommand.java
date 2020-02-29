package frc.robot.commands.vision;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.VisionSubsystem;

public class StopVisionTrackingCommand extends InstantCommand {
  private static final VisionSubsystem VISION = RobotContainer.VISION;

  @Override
  public void initialize() {
    VISION.setTrackingEnabled(false);
    VISION.setCameraEnabled(false);
      SmartDashboard.putBoolean("Match/Locked On", false);
  }
}
