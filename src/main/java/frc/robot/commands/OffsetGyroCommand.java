package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class OffsetGyroCommand extends InstantCommand {
  private DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private double offset;

  public OffsetGyroCommand(double offset) {
    addRequirements(DRIVE);
    this.offset = offset;
  }

  @Override
  public void initialize() {
    DRIVE.offsetGyro(offset);
  }
}
