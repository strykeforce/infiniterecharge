package frc.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class ZeroDriveWheelsCommand extends InstantCommand {
  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;

  public ZeroDriveWheelsCommand() {
    addRequirements(driveSubsystem);
  }

  @Override
  public void initialize() {
    driveSubsystem.zeroSwerve();
  }
}
