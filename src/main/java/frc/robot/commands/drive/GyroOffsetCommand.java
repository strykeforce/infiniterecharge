package frc.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class GyroOffsetCommand extends InstantCommand {
  private boolean enabled;
  private static DriveSubsystem DRIVE = RobotContainer.DRIVE;

  public GyroOffsetCommand(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public void initialize() {
    DRIVE.setGyroOffset(enabled);
  }
}
