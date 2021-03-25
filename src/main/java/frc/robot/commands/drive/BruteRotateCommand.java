package frc.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class BruteRotateCommand extends CommandBase {
  private DriveSubsystem DRIVE = RobotContainer.DRIVE;

  private double rotation;
  private long durationMs;
  private long initTime;

  public BruteRotateCommand(double rotation, long durationMs) {
    addRequirements(DRIVE);

    this.rotation = rotation;
    this.durationMs = durationMs;
  }

  @Override
  public void initialize() {
    initTime = System.currentTimeMillis();
  }

  @Override
  public void execute() {
    DRIVE.drive(0, 0, rotation);
  }

  @Override
  public boolean isFinished() {
    return System.currentTimeMillis() - initTime > durationMs;
  }

  @Override
  public void end(boolean interrupted) {
    DRIVE.drive(0, 0, 0);
  }
}
