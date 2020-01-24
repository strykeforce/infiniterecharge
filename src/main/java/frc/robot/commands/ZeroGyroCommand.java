package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class ZeroGyroCommand extends InstantCommand {
  private DriveSubsystem DRIVE = RobotContainer.DRIVE;

  public ZeroGyroCommand() {
    addRequirements(DRIVE);
  }

  @Override
  public void initialize() {
    DRIVE.zeroGyro();
  }
}
