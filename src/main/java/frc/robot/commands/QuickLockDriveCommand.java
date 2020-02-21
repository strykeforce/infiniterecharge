package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class QuickLockDriveCommand extends InstantCommand {
  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;

  public QuickLockDriveCommand() {
    addRequirements(driveSubsystem);
  }

  @Override
  public void initialize() {
    driveSubsystem.xLockSwerveDrive();
  }
}
