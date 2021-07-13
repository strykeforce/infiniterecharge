package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class OffsetGyroCommand extends InstantCommand {
  private DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private double offsetDegs;

  public OffsetGyroCommand(double offsetDegs) {
    addRequirements(DRIVE);
    this.offsetDegs = offsetDegs;
  }

  @Override
  public void initialize() {
    DRIVE.setGyroOffset(new Rotation2d(Math.toRadians(offsetDegs)));
  }
}
